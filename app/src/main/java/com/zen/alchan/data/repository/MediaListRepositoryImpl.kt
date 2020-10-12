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
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.MediaListSort
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.MediaFilterData
import com.zen.alchan.helper.toMillis
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.MediaListStatus
import type.MediaType
import java.util.*
import kotlin.collections.ArrayList

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

    private val _addAnimeToPlanningResponse = SingleLiveEvent<Resource<AnimeListEntryMutation.Data>>()
    override val addAnimeToPlanningResponse: LiveData<Resource<AnimeListEntryMutation.Data>>
        get() = _addAnimeToPlanningResponse

    override var animeFilterData: MediaFilterData? = null

    override var mangaFilterData: MediaFilterData? = null

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
        priority: Int?,
        updateCustomList: Boolean?
    ) {
        _updateMediaListEntryDetailResponse.postValue(Resource.Loading())

        mediaListDataSource.updateAnimeList(
            entryId, null, status, score, progress, repeat, isPrivate, notes, hiddenFromStatusLists, customLists, advancedScores, startedAt, completedAt, priority
        ).subscribeWith(object : Observer<Response<AnimeListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AnimeListEntryMutation.Data>) {
                handleUpdateAnimeEntryResult(t, status, isUpdateDetail = true, isUpdateCustomList = updateCustomList ?: false)
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

    private fun handleUpdateAnimeEntryResult(t: Response<AnimeListEntryMutation.Data>, originStatus: MediaListStatus? = null, isUpdateDetail: Boolean = false, isUpdateCustomList: Boolean = false) {
        if (t.hasErrors()) {
            if (isUpdateDetail) {
                _updateMediaListEntryDetailResponse.postValue(Resource.Error(t.errors!![0].message))
            } else {
                _updateAnimeListEntryResponse.postValue(Resource.Error(t.errors!![0].message))
            }
        } else {
            var editedEntriesIndex = ArrayList<Int>()
            val editedListsIndex = ArrayList<Int>()
            val currentList = rawAnimeList
            currentList?.lists?.forEachIndexed { index, group ->
                val tempEntriesIndex = group.entries?.indexOfFirst { mediaList -> mediaList.id == t.data?.saveMediaListEntry?.id }
                if (tempEntriesIndex != null && tempEntriesIndex != -1) {
                    editedEntriesIndex.add(tempEntriesIndex)
                    editedListsIndex.add(index)
                }
            }

            if (!editedListsIndex.isNullOrEmpty() && !editedEntriesIndex.isNullOrEmpty()) {
                if (isUpdateCustomList || (originStatus != null && currentList?.lists!![editedListsIndex[0]].entries!![editedEntriesIndex[0]].status != originStatus)) {
                    // if status is changed, reload list
                    retrieveAnimeListData()
                } else {
                    val newCollection = ArrayList(currentList?.lists!!)

                    editedListsIndex.forEachIndexed { index, value ->
                        val newGroup = newCollection[value]
                        val newMediaList = ArrayList(newGroup.entries!!)

                        val tempTags = newMediaList[editedEntriesIndex[index]].media?.tags
                        val tempNextAiringEp =  newMediaList[editedEntriesIndex[index]].media?.nextAiringEpisode

                        newMediaList[editedEntriesIndex[index]] = Converter.convertMediaList(t.data?.saveMediaListEntry!!)

                        // Needed because bugs in AniList where mutation won't return Tags
                        if (!tempTags.isNullOrEmpty()) {
                            newMediaList[editedEntriesIndex[index]].media?.tags = tempTags
                        }

                        // Needed because bugs in AniList where mutation won't return NextAiringEpisode
                        if (tempNextAiringEp != null) {
                            newMediaList[editedEntriesIndex[index]].media?.nextAiringEpisode = tempNextAiringEp
                        }

                        newGroup.entries = newMediaList
                        newCollection[value] = newGroup
                    }

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
            if (animeFilterData != null && animeFilterData?.selectedMediaListSort != null) {
                rowOrderEnum = animeFilterData?.selectedMediaListSort!!
            }
        } else if (mediaType == MediaType.MANGA) {
            if (mangaFilterData != null && mangaFilterData?.selectedMediaListSort != null) {
                rowOrderEnum = mangaFilterData?.selectedMediaListSort!!
            }
        }

        val orderByDescending = if (mediaType == MediaType.ANIME) {
            animeFilterData?.selectedMediaListOrderByDescending != false
        } else {
            mangaFilterData?.selectedMediaListOrderByDescending != false
        }

        val sortedByTitle = entries.sortedWith(compareBy { it.media?.title?.userPreferred?.toLowerCase(Locale.US) })

        return when (rowOrderEnum) {
            MediaListSort.TITLE -> {
                val orderByDescendingSpecialCase = if (mediaType == MediaType.ANIME) {
                    animeFilterData?.selectedMediaListOrderByDescending == true
                } else {
                    mangaFilterData?.selectedMediaListOrderByDescending == true
                }

                ArrayList(if (orderByDescendingSpecialCase) sortedByTitle.reversed() else sortedByTitle)
            }
            MediaListSort.SCORE -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it.score } else compareBy { it.score }))
            MediaListSort.PROGRESS -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it.progress } else compareBy { it.progress }))
            MediaListSort.LAST_UPDATED -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it.updatedAt } else compareBy { it.updatedAt }))
            MediaListSort.LAST_ADDED -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it.id } else compareBy { it.id }))
            MediaListSort.START_DATE -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it.startedAt.toMillis() } else compareBy { it.startedAt.toMillis() }))
            MediaListSort.COMPLETED_DATE -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it.completedAt.toMillis() } else compareBy { it.completedAt.toMillis() }))
            MediaListSort.RELEASE_DATE -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it.media?.startDate.toMillis() } else compareBy { it.media?.startDate.toMillis() }))
            MediaListSort.AVERAGE_SCORE -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it.media?.averageScore } else compareBy { it.media?.averageScore }))
            MediaListSort.POPULARITY -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it.media?.popularity } else compareBy { it.media?.popularity }))
            MediaListSort.PRIORITY -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it.priority } else compareBy { it.priority }))
            MediaListSort.NEXT_AIRING -> {
                val orderByDescendingSpecialCase = if (mediaType == MediaType.ANIME) {
                    animeFilterData?.selectedMediaListOrderByDescending == true
                } else {
                    mangaFilterData?.selectedMediaListOrderByDescending == true
                }

                ArrayList(sortedByTitle.sortedWith(if (orderByDescendingSpecialCase) {
                    compareByDescending { it.media?.nextAiringEpisode?.timeUntilAiring ?: Int.MIN_VALUE }
                } else {
                    compareBy { it.media?.nextAiringEpisode?.timeUntilAiring ?: Int.MAX_VALUE }
                }))
            }
        }
    }

    private fun filterAnimeListEntries(entries: List<MediaList>?): ArrayList<MediaList> {
        if (entries.isNullOrEmpty()) {
            return ArrayList()
        }

        if (animeFilterData == null) {
            return ArrayList(entries)
        }

        val filteredList = ArrayList<MediaList>()

        if (animeFilterData != null) {
            entries.forEach entries@{
                if (!animeFilterData?.selectedFormats.isNullOrEmpty() && animeFilterData?.selectedFormats?.contains(it.media?.format) != true) {
                    return@entries
                }

                if (!animeFilterData?.selectedStatuses.isNullOrEmpty() && animeFilterData?.selectedStatuses?.contains(it.media?.status) != true) {
                    return@entries
                }

                if (!animeFilterData?.selectedSources.isNullOrEmpty() &&  animeFilterData?.selectedSources?.contains(it.media?.source) != true) {
                    return@entries
                }

                if (animeFilterData?.selectedCountry != null && animeFilterData?.selectedCountry?.name != it.media?.countryOfOrigin) {
                    return@entries
                }

                if (animeFilterData?.selectedSeason != null && animeFilterData?.selectedSeason != it.media?.season) {
                    return@entries
                }

                if (animeFilterData?.selectedYear != null &&
                    (it.media?.startDate?.year == null ||
                    animeFilterData?.selectedYear?.minValue?.toString()?.substring(0..3)?.toInt()!! > it.media?.startDate?.year!! ||
                    animeFilterData?.selectedYear?.maxValue?.toString()?.substring(0..3)?.toInt()!! < it.media?.startDate?.year!!)
                ) {
                    return@entries
                }

                if (!animeFilterData?.selectedGenres.isNullOrEmpty() &&
                    (it.media?.genres.isNullOrEmpty() ||
                    !it.media?.genres!!.containsAll(animeFilterData?.selectedGenres!!))
                ) {
                    return@entries
                }

                if (!animeFilterData?.selectedExcludedGenres.isNullOrEmpty() && !it.media?.genres.isNullOrEmpty()) {
                    animeFilterData?.selectedExcludedGenres?.forEach { excludedGenre ->
                        if (it.media?.genres?.contains(excludedGenre) == true) {
                            return@entries
                        }
                    }
                }

                val mediaTagNames = it.media?.tags?.filterNotNull()?.map { tag -> tag.name }

                if (!animeFilterData?.selectedTagNames.isNullOrEmpty() &&
                    (mediaTagNames.isNullOrEmpty() ||
                    !mediaTagNames.containsAll(animeFilterData?.selectedTagNames!!))
                ) {
                    return@entries
                }

                if (!animeFilterData?.selectedExcludedTagNames.isNullOrEmpty() && !mediaTagNames.isNullOrEmpty()) {
                    animeFilterData?.selectedExcludedTagNames?.forEach { excludedTag ->
                        if (mediaTagNames.contains(excludedTag)) {
                            return@entries
                        }
                    }
                }

                if (!animeFilterData?.selectedLicensed.isNullOrEmpty()) {
                    if (it.media?.externalLinks.isNullOrEmpty()) {
                        return@entries
                    }

                    var hasSelectedLicense = false

                    it.media?.externalLinks?.forEach license@{ externalLink ->
                        if (animeFilterData?.selectedLicensed?.contains(Constant.EXTERNAL_LINK_MAP[externalLink?.site]) == true) {
                            hasSelectedLicense = true
                            return@license
                        }
                    }

                    if (!hasSelectedLicense) {
                        return@entries
                    }
                }

                if (animeFilterData?.selectedEpisodes != null &&
                    (it.media?.episodes == null ||
                    animeFilterData?.selectedEpisodes?.minValue ?: 0 > it.media?.episodes ?: 0 ||
                    animeFilterData?.selectedEpisodes?.maxValue ?: 0 < it.media?.episodes ?: 0)
                ) {
                    return@entries
                }

                if (animeFilterData?.selectedDuration != null &&
                    (it.media?.duration == null ||
                    animeFilterData?.selectedDuration?.minValue ?: 0 > it.media?.duration ?: 0 ||
                    animeFilterData?.selectedDuration?.maxValue ?: 0 < it.media?.duration ?: 0)
                ) {
                    return@entries
                }

                if (animeFilterData?.selectedAverageScore != null &&
                    (it.media?.averageScore == null ||
                    animeFilterData?.selectedAverageScore?.minValue ?: 0 > it.media?.averageScore ?: 0 ||
                    animeFilterData?.selectedAverageScore?.maxValue ?: 0 < it.media?.averageScore ?: 0)
                ) {
                    return@entries
                }

                if (animeFilterData?.selectedPopularity != null &&
                    (it.media?.popularity == null ||
                    animeFilterData?.selectedPopularity?.minValue ?: 0 > it.media?.popularity ?: 0 ||
                    animeFilterData?.selectedPopularity?.maxValue ?: 0 < it.media?.popularity ?: 0)
                ) {
                    return@entries
                }

                if (animeFilterData?.selectedUserScore != null &&
                    (it.score == null ||
                    animeFilterData?.selectedUserScore?.minValue ?: 0 > it.score?.toInt() ?: 0 ||
                    animeFilterData?.selectedUserScore?.maxValue ?: 0 < it.score?.toInt() ?: 0)
                ) {
                    return@entries
                }

                if (animeFilterData?.selectedUserStartYear != null &&
                    (it.startedAt?.year == null ||
                    animeFilterData?.selectedUserStartYear?.minValue ?: 0 > it.startedAt?.year ?: 0 ||
                    animeFilterData?.selectedUserStartYear?.maxValue ?: 0 < it.startedAt?.year ?: 0)
                ) {
                    return@entries
                }

                if (animeFilterData?.selectedUserFinishYear != null &&
                    (it.completedAt?.year == null ||
                    animeFilterData?.selectedUserFinishYear?.minValue ?: 0 > it.completedAt?.year ?: 0 ||
                    animeFilterData?.selectedUserFinishYear?.maxValue ?: 0 < it.completedAt?.year ?: 0)
                ) {
                    return@entries
                }

                if (animeFilterData?.selectedUserPriority != null &&
                    (it.priority == null ||
                    animeFilterData?.selectedUserPriority?.minValue ?: 0 > it.priority ?: 0 ||
                    animeFilterData?.selectedUserPriority?.maxValue ?: 0 < it.priority ?: 0)
                ) {
                    return@entries
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

        if (mangaFilterData == null) {
            return ArrayList(entries)
        }

        val filteredList = ArrayList<MediaList>()

        if (mangaFilterData != null) {
            entries.forEach entries@{
                if (!mangaFilterData?.selectedFormats.isNullOrEmpty() && mangaFilterData?.selectedFormats?.contains(it.media?.format) != true) {
                    return@entries
                }

                if (!mangaFilterData?.selectedStatuses.isNullOrEmpty() && mangaFilterData?.selectedStatuses?.contains(it.media?.status) != true) {
                    return@entries
                }

                if (!mangaFilterData?.selectedSources.isNullOrEmpty() &&  mangaFilterData?.selectedSources?.contains(it.media?.source) != true) {
                    return@entries
                }

                if (mangaFilterData?.selectedCountry != null && mangaFilterData?.selectedCountry?.name != it.media?.countryOfOrigin) {
                    return@entries
                }

                if (mangaFilterData?.selectedYear != null &&
                    (it.media?.startDate?.year == null ||
                    mangaFilterData?.selectedYear?.minValue?.toString()?.substring(0..3)?.toInt()!! > it.media?.startDate?.year!! ||
                    mangaFilterData?.selectedYear?.maxValue?.toString()?.substring(0..3)?.toInt()!! < it.media?.startDate?.year!!)
                ) {
                    return@entries
                }

                if (!mangaFilterData?.selectedGenres.isNullOrEmpty() &&
                    (it.media?.genres.isNullOrEmpty() ||
                    !it.media?.genres!!.containsAll(mangaFilterData?.selectedGenres!!))
                ) {
                    return@entries
                }

                if (!mangaFilterData?.selectedExcludedGenres.isNullOrEmpty() && !it.media?.genres.isNullOrEmpty()) {
                    mangaFilterData?.selectedExcludedGenres?.forEach { excludedGenre ->
                        if (it.media?.genres?.contains(excludedGenre) == true) {
                            return@entries
                        }
                    }
                }

                val mediaTagNames = it.media?.tags?.filterNotNull()?.map { tag -> tag.name }

                if (!mangaFilterData?.selectedTagNames.isNullOrEmpty() &&
                    (mediaTagNames.isNullOrEmpty() ||
                            !mediaTagNames.containsAll(mangaFilterData?.selectedTagNames!!))
                ) {
                    return@entries
                }

                if (!mangaFilterData?.selectedExcludedTagNames.isNullOrEmpty() && !mediaTagNames.isNullOrEmpty()) {
                    mangaFilterData?.selectedExcludedTagNames?.forEach { excludedTag ->
                        if (mediaTagNames.contains(excludedTag)) {
                            return@entries
                        }
                    }
                }

                if (!mangaFilterData?.selectedLicensed.isNullOrEmpty()) {
                    if (it.media?.externalLinks.isNullOrEmpty()) {
                        return@entries
                    }

                    var hasSelectedLicense = false

                    it.media?.externalLinks?.forEach license@{ externalLink ->
                        if (mangaFilterData?.selectedLicensed?.contains(Constant.EXTERNAL_LINK_MAP[externalLink?.site]) == true) {
                            hasSelectedLicense = true
                            return@license
                        }
                    }

                    if (!hasSelectedLicense) {
                        return@entries
                    }
                }

                if (mangaFilterData?.selectedChapters != null &&
                    (it.media?.chapters == null ||
                    mangaFilterData?.selectedChapters?.minValue ?: 0 > it.media?.chapters ?: 0 ||
                    mangaFilterData?.selectedChapters?.maxValue ?: 0 < it.media?.chapters ?: 0)
                ) {
                    return@entries
                }

                if (mangaFilterData?.selectedVolumes != null &&
                    (it.media?.volumes == null ||
                    mangaFilterData?.selectedVolumes?.minValue ?: 0 > it.media?.volumes ?: 0 ||
                    mangaFilterData?.selectedVolumes?.maxValue ?: 0 < it.media?.volumes ?: 0)
                ) {
                    return@entries
                }

                if (mangaFilterData?.selectedAverageScore != null &&
                    (it.media?.averageScore == null ||
                    mangaFilterData?.selectedAverageScore?.minValue ?: 0 > it.media?.averageScore ?: 0 ||
                    mangaFilterData?.selectedAverageScore?.maxValue ?: 0 < it.media?.averageScore ?: 0)
                ) {
                    return@entries
                }

                if (mangaFilterData?.selectedPopularity != null &&
                    (it.media?.popularity == null ||
                    mangaFilterData?.selectedPopularity?.minValue ?: 0 > it.media?.popularity ?: 0 ||
                    mangaFilterData?.selectedPopularity?.maxValue ?: 0 < it.media?.popularity ?: 0)
                ) {
                    return@entries
                }

                if (mangaFilterData?.selectedUserScore != null &&
                    (it.score == null ||
                    mangaFilterData?.selectedUserScore?.minValue ?: 0 > it.score?.toInt() ?: 0 ||
                    mangaFilterData?.selectedUserScore?.maxValue ?: 0 < it.score?.toInt() ?: 0)
                ) {
                    return@entries
                }

                if (mangaFilterData?.selectedUserStartYear != null &&
                    (it.startedAt?.year == null ||
                    mangaFilterData?.selectedUserStartYear?.minValue ?: 0 > it.startedAt?.year ?: 0 ||
                    mangaFilterData?.selectedUserStartYear?.maxValue ?: 0 < it.startedAt?.year ?: 0)
                ) {
                    return@entries
                }

                if (mangaFilterData?.selectedUserFinishYear != null &&
                    (it.completedAt?.year == null ||
                    mangaFilterData?.selectedUserFinishYear?.minValue ?: 0 > it.completedAt?.year ?: 0 ||
                    mangaFilterData?.selectedUserFinishYear?.maxValue ?: 0 < it.completedAt?.year ?: 0)
                ) {
                    return@entries
                }

                if (mangaFilterData?.selectedUserPriority != null &&
                    (it.priority == null ||
                    mangaFilterData?.selectedUserPriority?.minValue ?: 0 > it.priority ?: 0 ||
                    mangaFilterData?.selectedUserPriority?.maxValue ?: 0 < it.priority ?: 0)
                ) {
                    return@entries
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
        var defaultList: List<String?>? = null

        if (mediaType == MediaType.ANIME) {
            sectionOrder = userManager.viewerData?.mediaListOptions?.animeList?.sectionOrder
            customList = userManager.viewerData?.mediaListOptions?.animeList?.customLists
            defaultList = if (userManager.viewerData?.mediaListOptions?.animeList?.splitCompletedSectionByFormat == true) Constant.DEFAULT_SPLIT_ANIME_LIST_ORDER else Constant.DEFAULT_ANIME_LIST_ORDER
        } else if (mediaType == MediaType.MANGA) {
            sectionOrder = userManager.viewerData?.mediaListOptions?.mangaList?.sectionOrder
            customList = userManager.viewerData?.mediaListOptions?.mangaList?.customLists
            defaultList = if (userManager.viewerData?.mediaListOptions?.mangaList?.splitCompletedSectionByFormat == true) Constant.DEFAULT_SPLIT_MANGA_LIST_ORDER else Constant.DEFAULT_MANGA_LIST_ORDER
        }

        sectionOrder?.forEach { section ->
            val groupList = mediaListGroup.find { group -> group.name == section }
            if (groupList != null) {
                sortedList.add(groupList)
            }
        }

        customList?.forEach { custom ->
            val groupList = mediaListGroup.find { group -> group.name == custom && group.isCustomList == true }
            if (groupList != null && !sortedList.contains(groupList)) {
                sortedList.add(groupList)
            }
        }

        defaultList?.forEach { default ->
            val groupList = mediaListGroup.find { group -> group?.name == default && group.isCustomList == false }
            if (groupList != null && !sortedList.contains(groupList)) {
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
        priority: Int?,
        updateCustomList: Boolean?
    ) {
        _updateMediaListEntryDetailResponse.postValue(Resource.Loading())

        mediaListDataSource.updateMangaList(
            entryId, null, status, score, progress, progressVolumes, repeat, isPrivate, notes, hiddenFromStatusLists, customLists, advancedScores, startedAt, completedAt, priority
        ).subscribeWith(object : Observer<Response<MangaListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<MangaListEntryMutation.Data>) {
                handleUpdateMangaEntryResult(t, status, isUpdateDetail = true, isUpdateCustomList = updateCustomList ?: false)
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

    private fun handleUpdateMangaEntryResult(t: Response<MangaListEntryMutation.Data>, originStatus: MediaListStatus? = null, isUpdateDetail: Boolean = false, isUpdateCustomList: Boolean = false) {
        if (t.hasErrors()) {
            if (isUpdateDetail) {
                _updateMediaListEntryDetailResponse.postValue(Resource.Error(t.errors!![0].message))
            } else {
                _updateMangaListEntryResponse.postValue(Resource.Error(t.errors!![0].message))
            }
        } else {
            val editedEntriesIndex = ArrayList<Int>()
            val editedListsIndex = ArrayList<Int>()
            val currentList = rawMangaList
            currentList?.lists?.forEachIndexed { index, group ->
                val tempEntriesIndex = group.entries?.indexOfFirst { mediaList -> mediaList.id == t.data?.saveMediaListEntry?.id }
                if (tempEntriesIndex != null && tempEntriesIndex != -1) {
                    editedEntriesIndex.add(tempEntriesIndex)
                    editedListsIndex.add(index)
                }
            }

            if (!editedListsIndex.isNullOrEmpty() && !editedEntriesIndex.isNullOrEmpty()) {
                if (isUpdateCustomList || (originStatus != null && currentList?.lists!![editedListsIndex[0]].entries!![editedEntriesIndex[0]].status != originStatus)) {
                    // if status is changed, reload list
                    retrieveMangaListData()
                } else {
                    val newCollection = ArrayList(currentList?.lists!!)

                    editedListsIndex.forEachIndexed { index, value ->
                        val newGroup = newCollection[value]
                        val newMediaList = ArrayList(newGroup.entries!!)

                        val tempTags = newMediaList[editedEntriesIndex[index]].media?.tags
                        val tempNextAiringEp =  newMediaList[editedEntriesIndex[index]].media?.nextAiringEpisode

                        newMediaList[editedEntriesIndex[index]] = Converter.convertMediaList(t.data?.saveMediaListEntry!!)

                        // Needed because bugs in AniList where mutation won't return Tags
                        if (!tempTags.isNullOrEmpty()) {
                            newMediaList[editedEntriesIndex[index]].media?.tags = tempTags
                        }

                        // Needed because bugs in AniList where mutation won't return NextAiringEpisode
                        // Manga has no NextAiringEpisode anyway, this will be skipped
                        if (tempNextAiringEp != null) {
                            newMediaList[editedEntriesIndex[index]].media?.nextAiringEpisode = tempNextAiringEp
                        }

                        newGroup.entries = newMediaList
                        newCollection[value] = newGroup
                    }

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

    override fun handleNewFilter(newFilterData: MediaFilterData?, mediaType: MediaType) {
        if (mediaType == MediaType.ANIME) {
            animeFilterData = newFilterData
            notifyLiveDataFromRawAnimeList(true)
        } else if (mediaType == MediaType.MANGA) {
            mangaFilterData = newFilterData
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

    @SuppressLint("CheckResult")
    override fun addAnimeToPlanning(mediaId: Int) {
        _addAnimeToPlanningResponse.postValue(Resource.Loading())

        mediaListDataSource.addAnimeToPlanning(mediaId).subscribeWith(object : Observer<Response<AnimeListEntryMutation.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<AnimeListEntryMutation.Data>) {
                if (t.hasErrors()) {
                    _addAnimeToPlanningResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    retrieveAnimeListData()
                    _addAnimeToPlanningResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _addAnimeToPlanningResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }
}