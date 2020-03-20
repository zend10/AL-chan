package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.MediaListDataSource
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.data.response.MediaListCollection
import com.zen.alchan.data.response.MediaListGroup
import com.zen.alchan.helper.libs.SingleLiveEvent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.MediaListStatus

class MediaListRepositoryImpl(private val mediaListDataSource: MediaListDataSource,
                              private val userManager: UserManager
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

    override fun getAnimeListData() {

    }

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
                    var animeListCollection = Converter.convertMediaListCollection(t.data()?.MediaListCollection())

                    val groupListWithSortedEntries = ArrayList<MediaListGroup>()
                    animeListCollection.lists?.forEach { list ->
                        list.entries = sortMediaListEntries(list.entries)
                        groupListWithSortedEntries.add(list)
                    }

                    animeListCollection = MediaListCollection(sortAnimeListGrouping(groupListWithSortedEntries))

                    _animeListData.postValue(animeListCollection)
                    _animeListDataResponse.postValue(Resource.Success(true))
                }
            }

            override fun onError(e: Throwable) {
                _animeListDataResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
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

    private fun sortMediaListEntries(entries: List<MediaList>?): ArrayList<MediaList> {
        if (entries.isNullOrEmpty()) {
            return ArrayList()
        }

        return when (userManager.viewerData?.mediaListOptions?.rowOrder) {
            "title" -> ArrayList(entries.sortedWith(compareBy { it.media?.title?.userPreferred }))
            "score" -> ArrayList(entries.sortedWith(compareByDescending { it.score }))
            "updatedAt" -> ArrayList(entries.sortedWith(compareByDescending { it.updatedAt }))
            "id" -> ArrayList(entries.sortedWith(compareByDescending { it.id }))
            else -> ArrayList(entries.sortedWith(compareBy { it.media?.title?.userPreferred }))
        }
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
            val currentList = animeListData.value
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
                    _animeListData.postValue(currentList)
                }
            }

            _updateAnimeListEntryResponse.postValue(Resource.Success(true))
        }
    }
}