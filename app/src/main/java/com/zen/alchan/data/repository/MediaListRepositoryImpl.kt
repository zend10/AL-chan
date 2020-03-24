package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Response
import com.google.gson.Gson
import com.zen.alchan.data.datasource.MediaListDataSource
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.Media
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.data.response.MediaListCollection
import com.zen.alchan.data.response.MediaListGroup
import com.zen.alchan.helper.enums.MediaListSort
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.MediaFilteredData
import com.zen.alchan.helper.toMillis
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.MediaListStatus

class MediaListRepositoryImpl(private val mediaListDataSource: MediaListDataSource,
                              private val userManager: UserManager,
                              private val gson: Gson
) : MediaListRepository {

    private val _animeListDataResponse = SingleLiveEvent<Resource<Boolean>>()
    override val animeListDataResponse: LiveData<Resource<Boolean>>
        get() = _animeListDataResponse

    private val _animeListData = MutableLiveData<MediaListCollection>()
    override val animeListData: LiveData<MediaListCollection>
        get() = _animeListData

    private val _updateAnimeListEntryResponse = SingleLiveEvent<Resource<Boolean>>()
    override val updateAnimeListEntryResponse: LiveData<Resource<Boolean>>
        get() = _updateAnimeListEntryResponse

    override var filteredData: MediaFilteredData? = null

    // before filtered and sorted
    private var rawAnimeList: MediaListCollection? = null

    @SuppressLint("CheckResult")
    override fun retrieveAnimeListData() {
        if (userManager.viewerData?.id == null) {
            return
        }

        _animeListDataResponse.postValue(Resource.Loading())

        mediaListDataSource.getAnimeListData(userManager.viewerData?.id!!).subscribeWith(object : Observer<Response<AnimeListCollectionQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AnimeListCollectionQuery.Data>) {
                if (t.hasErrors()) {
                    _animeListDataResponse.postValue(Resource.Error(t.errors()[0].message()!!))
                } else {
                    rawAnimeList = Converter.convertMediaListCollection(t.data()?.MediaListCollection())
                    notifyLiveDataFromRawAnimeList(true)
                }
            }

            override fun onError(e: Throwable) {
                _animeListDataResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun updateAnimeProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        progress: Int
    ) {
        _updateAnimeListEntryResponse.postValue(Resource.Loading())

        mediaListDataSource.updateAnimeProgress(entryId, status, repeat, progress).subscribeWith(object : Observer<Response<AnimeListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AnimeListEntryMutation.Data>) {
                handleUpdateAnimeEntryResult(t, status)
            }

            override fun onError(e: Throwable) {
                _updateAnimeListEntryResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun updateAnimeScore(entryId: Int, score: Double, advancedScores: List<Double>?) {
        _updateAnimeListEntryResponse.postValue(Resource.Loading())

        mediaListDataSource.updateAnimeScore(entryId, score, advancedScores).subscribeWith(object : Observer<Response<AnimeListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AnimeListEntryMutation.Data>) {
                handleUpdateAnimeEntryResult(t)
            }

            override fun onError(e: Throwable) {
                _updateAnimeListEntryResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    private fun handleUpdateAnimeEntryResult(t: Response<AnimeListEntryMutation.Data>, originStatus: MediaListStatus? = null) {
        if (t.hasErrors()) {
            _updateAnimeListEntryResponse.postValue(Resource.Error(t.errors()[0].message()!!))
        } else {
            var editedEntriesIndex: Int? = null
            var editedListsIndex: Int? = null
            val currentList = rawAnimeList
            currentList?.lists?.forEachIndexed { index, group ->
                if (editedEntriesIndex == null || editedEntriesIndex == -1) {
                    editedEntriesIndex = group.entries?.indexOfFirst { mediaList -> mediaList.id == t.data()?.SaveMediaListEntry()?.id() }
                    editedListsIndex = index
                }
            }

            if (editedListsIndex != null && editedEntriesIndex != null && editedEntriesIndex != -1) {
                if (originStatus != null && currentList?.lists!![editedListsIndex!!].entries!![editedEntriesIndex!!].status != originStatus) {
                    // if status is changed, reload list
                    retrieveAnimeListData()
                } else {
                    val newCollection = ArrayList(currentList?.lists!!)
                    val newGroup = newCollection[editedListsIndex!!]
                    val newMediaList = ArrayList(newGroup.entries!!)

                    val tempNextAiringEp =  newMediaList[editedEntriesIndex!!].media?.nextAiringEpisode

                    newMediaList[editedEntriesIndex!!] = Converter.convertMediaListFromAnimeListMutation(t.data()?.SaveMediaListEntry()!!)

                    // Needed because bugs in AniList where mutation won't return NextAiringEpisode
                    if (tempNextAiringEp != null) {
                        newMediaList[editedEntriesIndex!!].media?.nextAiringEpisode = tempNextAiringEp
                    }

                    newGroup.entries = newMediaList
                    newCollection[editedListsIndex!!] = newGroup
                    currentList.lists = newCollection
                    rawAnimeList = currentList

                    notifyLiveDataFromRawAnimeList()
                }
            }

            _updateAnimeListEntryResponse.postValue(Resource.Success(true))
        }
    }

    override fun handleNewFilter(newFilteredData: MediaFilteredData?) {
        filteredData = newFilteredData
        notifyLiveDataFromRawAnimeList(true)
    }

    private fun sortMediaListEntries(entries: List<MediaList>?): ArrayList<MediaList> {
        if (entries.isNullOrEmpty()) {
            return ArrayList()
        }

        var rowOrderEnum = when(userManager.viewerData?.mediaListOptions?.rowOrder) {
            "title" -> MediaListSort.TITLE
            "score" -> MediaListSort.SCORE
            "updatedAt" -> MediaListSort.LAST_UPDATED
            "id" -> MediaListSort.LAST_ADDED
            else -> MediaListSort.TITLE
        }

        if (filteredData != null && filteredData?.selectedSort != null) {
            rowOrderEnum = filteredData?.selectedSort!!
        }

        return when (rowOrderEnum) {
            MediaListSort.TITLE -> ArrayList(entries.sortedWith(compareBy { it.media?.title?.userPreferred }))
            MediaListSort.SCORE -> ArrayList(entries.sortedWith(compareByDescending { it.score }))
            MediaListSort.PROGRESS -> ArrayList(entries.sortedWith(compareByDescending { it.progress }))
            MediaListSort.LAST_UPDATED -> ArrayList(entries.sortedWith(compareByDescending { it.updatedAt }))
            MediaListSort.LAST_ADDED -> ArrayList(entries.sortedWith(compareByDescending { it.id }))
            MediaListSort.START_DATE -> ArrayList(entries.sortedWith(compareByDescending { it.startedAt.toMillis() }))
            MediaListSort.COMPLETED_DATE -> ArrayList(entries.sortedWith(compareByDescending { it.completedAt.toMillis() }))
            MediaListSort.RELEASE_DATE -> ArrayList(entries.sortedWith(compareByDescending { it.media?.startDate.toMillis() }))
            MediaListSort.AVERAGE_SCORE -> ArrayList(entries.sortedWith(compareByDescending { it.media?.averageScore }))
            MediaListSort.POPULARITY -> ArrayList(entries.sortedWith(compareByDescending { it.media?.popularity }))
        }
    }

    private fun filterMediaListEntries(entries: List<MediaList>?): ArrayList<MediaList> {
        if (entries.isNullOrEmpty()) {
            return ArrayList()
        }

        if (filteredData == null) {
            return ArrayList(entries)
        }

        val filteredList = ArrayList<MediaList>()

        if (filteredData != null) {
            entries.forEach {
                if (filteredData?.selectedFormat != null && it.media?.format != filteredData?.selectedFormat) {
                    return@forEach
                }

                if (filteredData?.selectedYear != null && it.media?.seasonYear != filteredData?.selectedYear) {
                    return@forEach
                }

                if (filteredData?.selectedSeason != null && it.media?.season != filteredData?.selectedSeason) {
                    return@forEach
                }

                if (filteredData?.selectedCountry != null && it.media?.countryOfOrigin != filteredData?.selectedCountry?.name) {
                    return@forEach
                }

                if (filteredData?.selectedStatus != null && it.media?.status != filteredData?.selectedStatus) {
                    return@forEach
                }

                if (filteredData?.selectedSource != null && it.media?.source != filteredData?.selectedSource) {
                    return@forEach
                }

                if (!filteredData?.selectedGenreList.isNullOrEmpty() && !it.media?.genres.isNullOrEmpty() && !it.media?.genres!!.containsAll(filteredData?.selectedGenreList!!)) {
                    return@forEach
                }

                filteredList.add(it)
            }
        }

        return filteredList
    }

    private fun sortAnimeListGrouping(animeListGroup: List<MediaListGroup>?): ArrayList<MediaListGroup> {
        if (animeListGroup.isNullOrEmpty()) {
            return ArrayList()
        }

        val sortedList = ArrayList<MediaListGroup>()

        val sectionOrder = userManager.viewerData?.mediaListOptions?.animeList?.sectionOrder
        val customList = userManager.viewerData?.mediaListOptions?.animeList?.customLists

        sectionOrder?.forEach { section ->
            val groupList = animeListGroup.find { group -> group.name == section && group.isCustomList == false }
            if (groupList != null) {
                sortedList.add(groupList)
            }
        }

        customList?.forEach { custom ->
            val groupList = animeListGroup.find { group -> group.name == custom && group.isCustomList == true }
            if (groupList != null) {
                sortedList.add(groupList)
            }
        }

        return sortedList
    }

    private fun notifyLiveDataFromRawAnimeList(isStopLoading: Boolean = false) {
        // junk code here but the easiest for now
        var animeListCollection = gson.fromJson(gson.toJson(rawAnimeList), MediaListCollection::class.java)

        val groupListWithSortedEntries = ArrayList<MediaListGroup>()
        animeListCollection.lists?.forEach { list ->
            list.entries = sortMediaListEntries(list.entries)
            list.entries = filterMediaListEntries(list.entries)
            groupListWithSortedEntries.add(list)
        }

        animeListCollection = MediaListCollection(sortAnimeListGrouping(groupListWithSortedEntries))

        _animeListData.postValue(animeListCollection)
        if (isStopLoading) {
            _animeListDataResponse.postValue(Resource.Success(true))
        }
    }
}