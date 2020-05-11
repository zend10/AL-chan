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
import com.zen.alchan.data.response.FuzzyDate
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
import type.MediaType

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

    private val _mangaListDataResponse = SingleLiveEvent<Resource<Boolean>>()
    override val mangaListDataResponse: LiveData<Resource<Boolean>>
        get() = _mangaListDataResponse

    private val _mangaListData = MutableLiveData<MediaListCollection>()
    override val mangaListData: LiveData<MediaListCollection>
        get() = _mangaListData

    private val _updateMangaListEntryResponse = SingleLiveEvent<Resource<Boolean>>()
    override val updateMangaListEntryResponse: LiveData<Resource<Boolean>>
        get() = _updateMangaListEntryResponse

    private val _mediaListDataDetailResponse = SingleLiveEvent<Resource<MediaList>>()
    override val mediaListDataDetailResponse: LiveData<Resource<MediaList>>
        get() = _mediaListDataDetailResponse

    private val _updateMediaListEntryDetailResponse = SingleLiveEvent<Resource<Boolean>>()
    override val updateMediaListEntryDetailResponse: LiveData<Resource<Boolean>>
        get() = _updateMediaListEntryDetailResponse

    private val _deleteMediaListEntryResponse = SingleLiveEvent<Resource<Boolean>>()
    override val deleteMediaListEntryResponse: LiveData<Resource<Boolean>>
        get() = _deleteMediaListEntryResponse

    override var animeFilteredData: MediaFilteredData? = null

    override var mangaFilteredData: MediaFilteredData? = null

    // to store anime list before filtered and sorted
    private var rawAnimeList: MediaListCollection? = null

    // to store manga list before filtered and sorted
    private var rawMangaList: MediaListCollection? = null

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
                    _animeListDataResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    rawAnimeList = Converter.convertMediaListCollection(t.data?.mediaListCollection)
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
    override fun retrieveAnimeListDataDetail(entryId: Int) {
        if (userManager.viewerData?.id == null) {
            return
        }

        _mediaListDataDetailResponse.postValue(Resource.Loading())

        mediaListDataSource.getAnimeListDataDetail(entryId, userManager.viewerData?.id!!).subscribeWith(object : Observer<Response<AnimeListQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AnimeListQuery.Data>) {
                if (t.hasErrors()) {
                    _mediaListDataDetailResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _mediaListDataDetailResponse.postValue(Resource.Success(Converter.convertMediaList(t.data?.mediaList!!)))
                }
            }

            override fun onError(e: Throwable) {
                _mediaListDataDetailResponse.postValue(Resource.Error(e.localizedMessage))
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

    @SuppressLint("CheckResult")
    override fun updateAnimeList(
        entryId: Int,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        repeat: Int,
        isPrivate: Boolean,
        notes: String?,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?,
        priority: Int?
    ) {
        _updateMediaListEntryDetailResponse.postValue(Resource.Loading())

        mediaListDataSource.updateAnimeList(
            entryId, null, status, score, progress, repeat, isPrivate, notes, hiddenFromStatusLists, customLists, advancedScores, startedAt, completedAt, priority
        ).subscribeWith(object : Observer<Response<AnimeListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AnimeListEntryMutation.Data>) {
                handleUpdateAnimeEntryResult(t, status, isUpdateDetail = true)
            }

            override fun onError(e: Throwable) {
                _updateMediaListEntryDetailResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun addAnimeList(
        mediaId: Int,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        repeat: Int,
        isPrivate: Boolean,
        notes: String?,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?,
        priority: Int?
    ) {
        _updateMediaListEntryDetailResponse.postValue(Resource.Loading())

        mediaListDataSource.updateAnimeList(
            null, mediaId, status, score, progress, repeat, isPrivate, notes, hiddenFromStatusLists, customLists, advancedScores, startedAt, completedAt, priority
        ).subscribeWith(object : Observer<Response<AnimeListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AnimeListEntryMutation.Data>) {
                if (t.hasErrors()) {
                    _updateMediaListEntryDetailResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    retrieveAnimeListData()
                    _updateMediaListEntryDetailResponse.postValue(Resource.Success(true))
                }
            }

            override fun onError(e: Throwable) {
                _updateMediaListEntryDetailResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    private fun handleUpdateAnimeEntryResult(t: Response<AnimeListEntryMutation.Data>, originStatus: MediaListStatus? = null, isUpdateDetail: Boolean = false) {
        if (t.hasErrors()) {
            if (isUpdateDetail) {
                _updateMediaListEntryDetailResponse.postValue(Resource.Error(t.errors!![0].message))
            } else {
                _updateAnimeListEntryResponse.postValue(Resource.Error(t.errors!![0].message))
            }
        } else {
            var editedEntriesIndex: Int? = null
            var editedListsIndex: Int? = null
            val currentList = rawAnimeList
            currentList?.lists?.forEachIndexed { index, group ->
                if (editedEntriesIndex == null || editedEntriesIndex == -1) {
                    editedEntriesIndex = group.entries?.indexOfFirst { mediaList -> mediaList.id == t.data?.saveMediaListEntry?.id }
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

                    newMediaList[editedEntriesIndex!!] = Converter.convertMediaList(t.data?.saveMediaListEntry!!)

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

            if (isUpdateDetail) {
                _updateMediaListEntryDetailResponse.postValue(Resource.Success(true))
            } else {
                _updateAnimeListEntryResponse.postValue(Resource.Success(true))
            }
        }
    }

    private fun sortMediaListEntries(entries: List<MediaList>?, mediaType: MediaType): ArrayList<MediaList> {
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

        if (mediaType == MediaType.ANIME) {
            if (animeFilteredData != null && animeFilteredData?.selectedListSort != null) {
                rowOrderEnum = animeFilteredData?.selectedListSort!!
            }
        } else if (mediaType == MediaType.MANGA) {
            if (mangaFilteredData != null && mangaFilteredData?.selectedListSort != null) {
                rowOrderEnum = mangaFilteredData?.selectedListSort!!
            }
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
            MediaListSort.PRIORITY -> ArrayList(entries.sortedWith(compareByDescending { it.priority }))
        }
    }

    private fun filterAnimeListEntries(entries: List<MediaList>?): ArrayList<MediaList> {
        if (entries.isNullOrEmpty()) {
            return ArrayList()
        }

        if (animeFilteredData == null) {
            return ArrayList(entries)
        }

        val filteredList = ArrayList<MediaList>()

        if (animeFilteredData != null) {
            entries.forEach {
                if (animeFilteredData?.selectedFormat != null && it.media?.format != animeFilteredData?.selectedFormat) {
                    return@forEach
                }

                if (animeFilteredData?.selectedYear != null && it.media?.seasonYear != animeFilteredData?.selectedYear) {
                    return@forEach
                }

                if (animeFilteredData?.selectedSeason != null && it.media?.season != animeFilteredData?.selectedSeason) {
                    return@forEach
                }

                if (animeFilteredData?.selectedCountry != null && it.media?.countryOfOrigin != animeFilteredData?.selectedCountry?.name) {
                    return@forEach
                }

                if (animeFilteredData?.selectedStatus != null && it.media?.status != animeFilteredData?.selectedStatus) {
                    return@forEach
                }

                if (animeFilteredData?.selectedSource != null && it.media?.source != animeFilteredData?.selectedSource) {
                    return@forEach
                }

                if (!animeFilteredData?.selectedGenreList.isNullOrEmpty() && !it.media?.genres.isNullOrEmpty() && !it.media?.genres!!.containsAll(animeFilteredData?.selectedGenreList!!)) {
                    return@forEach
                }

                filteredList.add(it)
            }
        }

        return filteredList
    }

    private fun filterMangaListEntries(entries: List<MediaList>?): ArrayList<MediaList> {
        if (entries.isNullOrEmpty()) {
            return ArrayList()
        }

        if (mangaFilteredData == null) {
            return ArrayList(entries)
        }

        val filteredList = ArrayList<MediaList>()

        if (mangaFilteredData != null) {
            entries.forEach {
                if (mangaFilteredData?.selectedFormat != null && it.media?.format != mangaFilteredData?.selectedFormat) {
                    return@forEach
                }

                if (mangaFilteredData?.selectedYear != null && it.media?.seasonYear != mangaFilteredData?.selectedYear) {
                    return@forEach
                }

                if (mangaFilteredData?.selectedCountry != null && it.media?.countryOfOrigin != mangaFilteredData?.selectedCountry?.name) {
                    return@forEach
                }

                if (mangaFilteredData?.selectedStatus != null && it.media?.status != mangaFilteredData?.selectedStatus) {
                    return@forEach
                }

                if (mangaFilteredData?.selectedSource != null && it.media?.source != mangaFilteredData?.selectedSource) {
                    return@forEach
                }

                if (!mangaFilteredData?.selectedGenreList.isNullOrEmpty() && !it.media?.genres.isNullOrEmpty() && !it.media?.genres!!.containsAll(animeFilteredData?.selectedGenreList!!)) {
                    return@forEach
                }

                filteredList.add(it)
            }
        }

        return filteredList
    }

    private fun sortMediaListGrouping(mediaListGroup: List<MediaListGroup>?, mediaType: MediaType): ArrayList<MediaListGroup> {
        if (mediaListGroup.isNullOrEmpty()) {
            return ArrayList()
        }

        val sortedList = ArrayList<MediaListGroup>()

        var sectionOrder: List<String?>? = null
        var customList: List<String?>? = null

        if (mediaType == MediaType.ANIME) {
            sectionOrder = userManager.viewerData?.mediaListOptions?.animeList?.sectionOrder
            customList = userManager.viewerData?.mediaListOptions?.animeList?.customLists
        } else if (mediaType == MediaType.MANGA) {
            sectionOrder = userManager.viewerData?.mediaListOptions?.mangaList?.sectionOrder
            customList = userManager.viewerData?.mediaListOptions?.mangaList?.customLists
        }

        sectionOrder?.forEach { section ->
            val groupList = mediaListGroup.find { group -> group.name == section && group.isCustomList == false }
            if (groupList != null) {
                sortedList.add(groupList)
            }
        }

        customList?.forEach { custom ->
            val groupList = mediaListGroup.find { group -> group.name == custom && group.isCustomList == true }
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
            list.entries = sortMediaListEntries(list.entries, MediaType.ANIME)
            list.entries = filterAnimeListEntries(list.entries)
            groupListWithSortedEntries.add(list)
        }

        animeListCollection = MediaListCollection(sortMediaListGrouping(groupListWithSortedEntries, MediaType.ANIME))

        _animeListData.postValue(animeListCollection)
        if (isStopLoading) {
            _animeListDataResponse.postValue(Resource.Success(true))
        }
    }

    @SuppressLint("CheckResult")
    override fun retrieveMangaListData() {
        if (userManager.viewerData?.id == null) {
            return
        }

        _mangaListDataResponse.postValue(Resource.Loading())

        mediaListDataSource.getMangaListData(userManager.viewerData?.id!!).subscribeWith(object : Observer<Response<MangaListCollectionQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<MangaListCollectionQuery.Data>) {
                if (t.hasErrors()) {
                    _mangaListDataResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    rawMangaList = Converter.convertMediaListCollection(t.data?.mediaListCollection)
                    notifyLiveDataFromRawMangaList(true)
                }
            }

            override fun onError(e: Throwable) {
                _mangaListDataResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun retrieveMangaListDataDetail(entryId: Int) {
        if (userManager.viewerData?.id == null) {
            return
        }

        _mediaListDataDetailResponse.postValue(Resource.Loading())

        mediaListDataSource.getMangaListDataDetail(entryId, userManager.viewerData?.id!!).subscribeWith(object : Observer<Response<MangaListQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<MangaListQuery.Data>) {
                if (t.hasErrors()) {
                    _mediaListDataDetailResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _mediaListDataDetailResponse.postValue(Resource.Success(Converter.convertMediaList(t.data?.mediaList!!)))
                }
            }

            override fun onError(e: Throwable) {
                _mediaListDataDetailResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun updateMangaProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        progress: Int?,
        progressVolumes: Int?
    ) {
        _updateMangaListEntryResponse.postValue(Resource.Loading())

        mediaListDataSource.updateMangaProgress(entryId, status, repeat, progress, progressVolumes).subscribeWith(object : Observer<Response<MangaListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<MangaListEntryMutation.Data>) {
                handleUpdateMangaEntryResult(t, status)
            }

            override fun onError(e: Throwable) {
                _updateMangaListEntryResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun updateMangaScore(entryId: Int, score: Double, advancedScores: List<Double>?) {
        _updateMangaListEntryResponse.postValue(Resource.Loading())

        mediaListDataSource.updateMangaScore(entryId, score, advancedScores).subscribeWith(object : Observer<Response<MangaListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<MangaListEntryMutation.Data>) {
                handleUpdateMangaEntryResult(t)
            }

            override fun onError(e: Throwable) {
                _updateMangaListEntryResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun updateMangaList(
        entryId: Int,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        progressVolumes: Int,
        repeat: Int,
        isPrivate: Boolean,
        notes: String?,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?,
        priority: Int?
    ) {
        _updateMediaListEntryDetailResponse.postValue(Resource.Loading())

        mediaListDataSource.updateMangaList(
            entryId, null, status, score, progress, progressVolumes, repeat, isPrivate, notes, hiddenFromStatusLists, customLists, advancedScores, startedAt, completedAt, priority
        ).subscribeWith(object : Observer<Response<MangaListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<MangaListEntryMutation.Data>) {
                handleUpdateMangaEntryResult(t, status, isUpdateDetail = true)
            }

            override fun onError(e: Throwable) {
                _updateMediaListEntryDetailResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun addMangaList(
        mediaId: Int,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        progressVolumes: Int,
        repeat: Int,
        isPrivate: Boolean,
        notes: String?,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?,
        priority: Int?
    ) {
        _updateMediaListEntryDetailResponse.postValue(Resource.Loading())

        mediaListDataSource.updateMangaList(
            null, mediaId, status, score, progress, progressVolumes, repeat, isPrivate, notes, hiddenFromStatusLists, customLists, advancedScores, startedAt, completedAt, priority
        ).subscribeWith(object : Observer<Response<MangaListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<MangaListEntryMutation.Data>) {
                if (t.hasErrors()) {
                    _updateMediaListEntryDetailResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    retrieveMangaListData()
                    _updateMediaListEntryDetailResponse.postValue(Resource.Success(true))
                }
            }

            override fun onError(e: Throwable) {
                _updateMediaListEntryDetailResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }

    private fun handleUpdateMangaEntryResult(t: Response<MangaListEntryMutation.Data>, originStatus: MediaListStatus? = null, isUpdateDetail: Boolean = false) {
        if (t.hasErrors()) {
            if (isUpdateDetail) {
                _updateMediaListEntryDetailResponse.postValue(Resource.Error(t.errors!![0].message))
            } else {
                _updateMangaListEntryResponse.postValue(Resource.Error(t.errors!![0].message))
            }
        } else {
            var editedEntriesIndex: Int? = null
            var editedListsIndex: Int? = null
            val currentList = rawMangaList
            currentList?.lists?.forEachIndexed { index, group ->
                if (editedEntriesIndex == null || editedEntriesIndex == -1) {
                    editedEntriesIndex = group.entries?.indexOfFirst { mediaList -> mediaList.id == t.data?.saveMediaListEntry?.id }
                    editedListsIndex = index
                }
            }

            if (editedListsIndex != null && editedEntriesIndex != null && editedEntriesIndex != -1) {
                if (originStatus != null && currentList?.lists!![editedListsIndex!!].entries!![editedEntriesIndex!!].status != originStatus) {
                    // if status is changed, reload list
                    retrieveMangaListData()
                } else {
                    val newCollection = ArrayList(currentList?.lists!!)
                    val newGroup = newCollection[editedListsIndex!!]
                    val newMediaList = ArrayList(newGroup.entries!!)

                    val tempNextAiringEp =  newMediaList[editedEntriesIndex!!].media?.nextAiringEpisode

                    newMediaList[editedEntriesIndex!!] = Converter.convertMediaList(t.data?.saveMediaListEntry!!)

                    // Needed because bugs in AniList where mutation won't return NextAiringEpisode
                    // Manga has no NextAiringEpisode anyway, this will be skipped
                    if (tempNextAiringEp != null) {
                        newMediaList[editedEntriesIndex!!].media?.nextAiringEpisode = tempNextAiringEp
                    }

                    newGroup.entries = newMediaList
                    newCollection[editedListsIndex!!] = newGroup
                    currentList.lists = newCollection
                    rawMangaList = currentList

                    notifyLiveDataFromRawMangaList()
                }
            }

            if (isUpdateDetail) {
                _updateMediaListEntryDetailResponse.postValue(Resource.Success(true))
            } else {
                _updateMangaListEntryResponse.postValue(Resource.Success(true))
            }
        }
    }

    private fun notifyLiveDataFromRawMangaList(isStopLoading: Boolean = false) {
        // junk code here but the easiest for now
        var mangaListCollection = gson.fromJson(gson.toJson(rawMangaList), MediaListCollection::class.java)

        val groupListWithSortedEntries = ArrayList<MediaListGroup>()
        mangaListCollection.lists?.forEach { list ->
            list.entries = sortMediaListEntries(list.entries, MediaType.MANGA)
            list.entries = filterMangaListEntries(list.entries)
            groupListWithSortedEntries.add(list)
        }

        mangaListCollection = MediaListCollection(sortMediaListGrouping(groupListWithSortedEntries, MediaType.MANGA))

        _mangaListData.postValue(mangaListCollection)
        if (isStopLoading) {
            _mangaListDataResponse.postValue(Resource.Success(true))
        }
    }

    override fun handleNewFilter(newFilteredData: MediaFilteredData?, mediaType: MediaType) {
        if (mediaType == MediaType.ANIME) {
            animeFilteredData = newFilteredData
            notifyLiveDataFromRawAnimeList(true)
        } else if (mediaType == MediaType.MANGA) {
            mangaFilteredData = newFilteredData
            notifyLiveDataFromRawMangaList(true)
        }
    }

    @SuppressLint("CheckResult")
    override fun deleteMediaList(entryId: Int, mediaType: MediaType) {
        _deleteMediaListEntryResponse.postValue(Resource.Loading())

        mediaListDataSource.deleteMediaList(entryId).subscribeWith(object : Observer<Response<DeleteMediaListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<DeleteMediaListEntryMutation.Data>) {
                if (t.hasErrors()) {
                    _deleteMediaListEntryResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    if (t.data?.deleteMediaListEntry?.deleted == true) {
                        if (mediaType == MediaType.ANIME) {
                            retrieveAnimeListData()
                        } else if (mediaType == MediaType.MANGA) {
                            retrieveMangaListData()
                        }
                        _deleteMediaListEntryResponse.postValue(Resource.Success(true))
                    } else {
                        _deleteMediaListEntryResponse.postValue(Resource.Error("Failed to delete this entry. Maybe try refreshing and try again."))
                    }
                }
            }

            override fun onError(e: Throwable) {
                _deleteMediaListEntryResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }
}