package com.zen.alchan.ui.filter

import com.zen.alchan.R
import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class FilterViewModel : BaseViewModel() {

    private val _sortBy = BehaviorSubject.createDefault(Sort.FOLLOW_LIST_SETTINGS)
    val sortBy: Observable<Sort>
        get() = _sortBy

    private val _orderByDescending = BehaviorSubject.createDefault(true)
    val orderByDescending: Observable<Boolean>
        get() = _orderByDescending

    private val _mediaFormats = BehaviorSubject.createDefault(listOf<MediaFormat>())
    val mediaFormats: Observable<List<MediaFormat>>
        get() = _mediaFormats

    private val _mediaStatuses = BehaviorSubject.createDefault(listOf<MediaStatus>())
    val mediaStatuses: Observable<List<MediaStatus>>
        get() = _mediaStatuses

    private val _mediaSources = BehaviorSubject.createDefault(listOf<MediaSource>())
    val mediaSources: Observable<List<MediaSource>>
        get() = _mediaSources

    private val _countries = BehaviorSubject.createDefault(listOf<Country>())
    val countries: Observable<List<Country>>
        get() = _countries

    private val _mediaSeasons = BehaviorSubject.createDefault(listOf<MediaSeason>())
    val mediaSeasons: Observable<List<MediaSeason>>
        get() = _mediaSeasons

    private val _releaseYears = BehaviorSubject.createDefault(NullableItem<Pair<Int, Int>>())
    val releaseYears: Observable<NullableItem<Pair<Int, Int>>>
        get() = _releaseYears

    private val _episodes = BehaviorSubject.createDefault(NullableItem<Pair<Int, Int>>())
    val episodes: Observable<NullableItem<Pair<Int, Int>>>
        get() = _episodes

    private val _durations = BehaviorSubject.createDefault(NullableItem<Pair<Int, Int>>())
    val durations: Observable<NullableItem<Pair<Int, Int>>>
        get() = _durations

    private val _averageScores = BehaviorSubject.createDefault(NullableItem<Pair<Int, Int>>())
    val averageScores: Observable<NullableItem<Pair<Int, Int>>>
        get() = _averageScores

    private val _popularity = BehaviorSubject.createDefault(NullableItem<Pair<Int, Int>>())
    val popularity: Observable<NullableItem<Pair<Int, Int>>>
        get() = _popularity

    private val _scores = BehaviorSubject.createDefault(NullableItem<Pair<Int, Int>>())
    val scores: Observable<NullableItem<Pair<Int, Int>>>
        get() = _scores

    private val _startYears = BehaviorSubject.createDefault(NullableItem<Pair<Int, Int>>())
    val startYears: Observable<NullableItem<Pair<Int, Int>>>
        get() = _startYears

    private val _completedYears = BehaviorSubject.createDefault(NullableItem<Pair<Int, Int>>())
    val completedYears: Observable<NullableItem<Pair<Int, Int>>>
        get() = _completedYears

    private val _priorities = BehaviorSubject.createDefault(NullableItem<Pair<Int, Int>>())
    val priorities: Observable<NullableItem<Pair<Int, Int>>>
        get() = _priorities






    private val _sortByList = PublishSubject.create<List<ListItem<Sort>>>()
    val sortByList: Observable<List<ListItem<Sort>>>
        get() = _sortByList

    private val _orderByList = PublishSubject.create<List<ListItem<Boolean>>>()
    val orderByList: Observable<List<ListItem<Boolean>>>
        get() = _orderByList

    private val _mediaFormatList = PublishSubject.create<Pair<List<ListItem<MediaFormat>>, ArrayList<Int>>>()
    val mediaFormatList: Observable<Pair<List<ListItem<MediaFormat>>, ArrayList<Int>>>
        get() = _mediaFormatList

    private val _mediaStatusList = PublishSubject.create<Pair<List<ListItem<MediaStatus>>, ArrayList<Int>>>()
    val mediaStatusList: Observable<Pair<List<ListItem<MediaStatus>>, ArrayList<Int>>>
        get() = _mediaStatusList

    private val _mediaSourceList = PublishSubject.create<Pair<List<ListItem<MediaSource>>, ArrayList<Int>>>()
    val mediaSourceList: Observable<Pair<List<ListItem<MediaSource>>, ArrayList<Int>>>
        get() = _mediaSourceList

    private val _countryList = PublishSubject.create<Pair<List<ListItem<Country>>, ArrayList<Int>>>()
    val countryList: Observable<Pair<List<ListItem<Country>>, ArrayList<Int>>>
        get() = _countryList

    private val _mediaSeasonList = PublishSubject.create<Pair<List<ListItem<MediaSeason>>, ArrayList<Int>>>()
    val mediaSeasonList: Observable<Pair<List<ListItem<MediaSeason>>, ArrayList<Int>>>
        get() = _mediaSeasonList

    var mediaType: MediaType = MediaType.MANGA
    var isUserList: Boolean = true

    private var currentMediaFilter = MediaFilter.EMPTY_MEDIA_FILTER

    override fun loadData() {
        loadOnce {

        }
    }

    fun updateSortBy(newSort: Sort) {
        currentMediaFilter.sort = newSort
        _sortBy.onNext(newSort)
    }

    fun updateOrderBy(shouldOrderByDescending: Boolean) {
        currentMediaFilter.orderByDescending = shouldOrderByDescending
        _orderByDescending.onNext(shouldOrderByDescending)
    }

    fun updateMediaFormats(newMediaFormats: List<MediaFormat>) {
        currentMediaFilter.mediaFormats = newMediaFormats.map { it.getAniListMediaFormat() }
        _mediaFormats.onNext(newMediaFormats)
    }

    fun updateMediaStatuses(newMediaStatuses: List<MediaStatus>) {
        currentMediaFilter.mediaStatuses = newMediaStatuses.map { it.getAniListMediaStatus() }
        _mediaStatuses.onNext(newMediaStatuses)
    }

    fun updateMediaSources(newMediaSources: List<MediaSource>) {
        currentMediaFilter.mediaSources = newMediaSources.map { it.getAniListMediaSource() }
        _mediaSources.onNext(newMediaSources)
    }

    fun updateCountries(newCountries: List<Country>) {
        currentMediaFilter.countries = newCountries
        _countries.onNext(newCountries)
    }

    fun updateMediaSeasons(newSeasons: List<MediaSeason>) {
        currentMediaFilter.mediaSeasons = newSeasons.map { it.getAniListMediaSeason() }
        _mediaSeasons.onNext(newSeasons)
    }

    fun loadSortByList() {
        val sortBy = ArrayList<ListItem<Sort>>()
        sortBy.addAll(Sort.values().map { ListItem(it.getStringResource(), it) })
        _sortByList.onNext(sortBy)
    }

    fun loadOrderByList() {
        val orderBy = ArrayList<ListItem<Boolean>>()
        orderBy.add(ListItem(R.string.descending, true))
        orderBy.add(ListItem(R.string.ascending, false))
        _orderByList.onNext(orderBy)
    }

    fun loadMediaFormats() {
        val animeFormats = listOf(
            MediaFormat.TV,
            MediaFormat.TV_SHORT,
            MediaFormat.MOVIE,
            MediaFormat.SPECIAL,
            MediaFormat.OVA,
            MediaFormat.ONA,
            MediaFormat.MUSIC
        )
        val mangaFormats = listOf(
            MediaFormat.MANGA,
            MediaFormat.ONE_SHOT,
            MediaFormat.NOVEL
        )
        val mediaFormats = when (mediaType) {
            MediaType.ANIME -> animeFormats
            MediaType.MANGA -> mangaFormats
        }

        val formats = ArrayList<ListItem<MediaFormat>>()
        formats.addAll(mediaFormats.map { ListItem(it.getFormatName(), listOf(), it) })

        val selectedIndex = ArrayList<Int>()
        _mediaFormats.value?.forEach {
            val index = mediaFormats.indexOf(it)
            if (index != -1)
                selectedIndex.add(index)
        }

        _mediaFormatList.onNext(formats to selectedIndex)
    }

    fun loadMediaStatuses() {
        val mediaStatuses = MediaStatus.values()
        val statuses = ArrayList<ListItem<MediaStatus>>()
        statuses.addAll(mediaStatuses.map { ListItem(it.getStatusName(), listOf(), it) })

        val selectedIndex = ArrayList<Int>()
        _mediaStatuses.value?.forEach {
            val index = mediaStatuses.indexOf(it)
            if (index != -1)
                selectedIndex.add(index)
        }

        _mediaStatusList.onNext(statuses to selectedIndex)
    }

    fun loadMediaSources() {
        val mediaSources = MediaSource.values()
        val sources = ArrayList<ListItem<MediaSource>>()
        sources.addAll(mediaSources.map { ListItem(it.getSourceName(), listOf(), it) })

        val selectedIndex = ArrayList<Int>()
        _mediaSources.value?.forEach {
            val index = mediaSources.indexOf(it)
            if (index != -1)
                selectedIndex.add(index)
        }

        _mediaSourceList.onNext(sources to selectedIndex)
    }

    fun loadCountries() {
        val mediaCountries = Country.values()
        val countries = ArrayList<ListItem<Country>>()
        countries.addAll(mediaCountries.map { ListItem(it.getCountryName(), listOf(), it) })

        val selectedIndex = ArrayList<Int>()
        _countries.value?.forEach {
            val index = mediaCountries.indexOf(it)
            if (index != -1)
                selectedIndex.add(index)
        }

        _countryList.onNext(countries to selectedIndex)
    }

    fun loadMediaSeasons() {
        val mediaSeasons = MediaSeason.values()
        val seasons = ArrayList<ListItem<MediaSeason>>()
        seasons.addAll(mediaSeasons.map { ListItem(it.getSeasonName(), listOf(), it) })

        val selectedIndex = ArrayList<Int>()
        _mediaSeasons.value?.forEach {
            val index = mediaSeasons.indexOf(it)
            if (index != -1)
                selectedIndex.add(index)
        }

        _mediaSeasonList.onNext(seasons to selectedIndex)
    }
}