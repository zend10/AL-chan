package com.zen.alchan.ui.filter

import com.zen.alchan.R
import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.pojo.SliderItem
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlin.math.min

class FilterViewModel : BaseViewModel() {

    private val _persistFilter = BehaviorSubject.createDefault(false)
    val persistFilter: Observable<Boolean>
        get() = _persistFilter

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

    private val _includedGenres = BehaviorSubject.createDefault(listOf<String>())
    val includedGenres: Observable<List<String>>
        get() = _includedGenres

    private val _excludedGenres = BehaviorSubject.createDefault(listOf<String>())
    val excludedGenres: Observable<List<String>>
        get() = _excludedGenres

    private val _includedTags = BehaviorSubject.createDefault(listOf<String>())
    val includedTags: Observable<List<String>>
        get() = _includedTags

    private val _excludedTags = BehaviorSubject.createDefault(listOf<String>())
    val excludedTags: Observable<List<String>>
        get() = _excludedTags

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

    private val _releaseYearsSliderItem = PublishSubject.create<SliderItem>()
    val releaseYearsSliderItem: Observable<SliderItem>
        get() = _releaseYearsSliderItem

    private val _episodesSliderItem = PublishSubject.create<SliderItem>()
    val episodesSliderItem: Observable<SliderItem>
        get() = _episodesSliderItem

    private val _durationsSliderItem = PublishSubject.create<SliderItem>()
    val durationsSliderItem: Observable<SliderItem>
        get() = _durationsSliderItem

    private val _averageScoresSliderItem = PublishSubject.create<SliderItem>()
    val averageScoresSliderItem: Observable<SliderItem>
        get() = _averageScoresSliderItem

    private val _popularitySliderItem = PublishSubject.create<SliderItem>()
    val popularitySliderItem: Observable<SliderItem>
        get() = _popularitySliderItem

    private val _scoresSliderItem = PublishSubject.create<SliderItem>()
    val scoresSliderItem: Observable<SliderItem>
        get() = _scoresSliderItem

    private val _startYearsSliderItem = PublishSubject.create<SliderItem>()
    val startYearsSliderItem: Observable<SliderItem>
        get() = _startYearsSliderItem

    private val _completedYearsSliderItem = PublishSubject.create<SliderItem>()
    val completedYearsSliderItem: Observable<SliderItem>
        get() = _completedYearsSliderItem

    private val _prioritiesYearsSliderItem = PublishSubject.create<SliderItem>()
    val prioritiesYearsSliderItem: Observable<SliderItem>
        get() = _prioritiesYearsSliderItem

    var mediaType: MediaType = MediaType.MANGA
    var isUserList: Boolean = true

    private var currentMediaFilter = MediaFilter.EMPTY_MEDIA_FILTER

    override fun loadData() {
        loadOnce {

        }
    }

    fun updatePersistFilter(shouldPersist: Boolean) {
        currentMediaFilter.persistFilter = shouldPersist
        _persistFilter.onNext(shouldPersist)
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

    fun updateReleaseYears(minYear: Int?, maxYear: Int?) {
        val releaseYears = if (minYear == null || maxYear == null) {
            currentMediaFilter.minYear = null
            currentMediaFilter.maxYear = null
            NullableItem<Pair<Int, Int>>(null)
        } else {
            currentMediaFilter.minYear = minYear
            currentMediaFilter.maxYear = maxYear
            NullableItem(Pair(minYear, maxYear))
        }

        _releaseYears.onNext(releaseYears)
    }

    fun updateEpisodes(minEpisode: Int?, maxEpisode: Int?) {
        val episodes = if (minEpisode == null || maxEpisode == null) {
            currentMediaFilter.minEpisodes = null
            currentMediaFilter.maxEpisodes = null
            NullableItem<Pair<Int, Int>>(null)
        } else {
            currentMediaFilter.minEpisodes = minEpisode
            currentMediaFilter.maxEpisodes = maxEpisode
            NullableItem(Pair(minEpisode, maxEpisode))
        }

        _episodes.onNext(episodes)
    }

    fun updateDurations(minDuration: Int?, maxDuration: Int?) {
        val durations = if (minDuration == null || maxDuration == null) {
            currentMediaFilter.minDuration = null
            currentMediaFilter.maxDuration = null
            NullableItem<Pair<Int, Int>>(null)
        } else {
            currentMediaFilter.minDuration = minDuration
            currentMediaFilter.maxDuration = maxDuration
            NullableItem(Pair(minDuration, maxDuration))
        }

        _durations.onNext(durations)
    }

    fun updateAverageScores(minAverageScore: Int?, maxAverageScore: Int?) {
        val averageScores = if (minAverageScore == null || maxAverageScore == null) {
            currentMediaFilter.minAverageScore = null
            currentMediaFilter.maxAverageScore = null
            NullableItem<Pair<Int, Int>>(null)
        } else {
            currentMediaFilter.minAverageScore = minAverageScore
            currentMediaFilter.maxAverageScore = maxAverageScore
            NullableItem(Pair(minAverageScore, maxAverageScore))
        }

        _averageScores.onNext(averageScores)
    }

    fun updatePopularity(minPopularity: Int?, maxPopularity: Int?) {
        val popularity = if (minPopularity == null || maxPopularity == null) {
            currentMediaFilter.minPopularity = null
            currentMediaFilter.maxPopularity = null
            NullableItem<Pair<Int, Int>>(null)
        } else {
            currentMediaFilter.minPopularity = minPopularity
            currentMediaFilter.maxPopularity = maxPopularity
            NullableItem(Pair(minPopularity, maxPopularity))
        }

        _popularity.onNext(popularity)
    }

    fun updateScores(minScore: Int?, maxScore: Int?) {
        val scores = if (minScore == null || maxScore == null) {
            currentMediaFilter.minUserScore = null
            currentMediaFilter.maxUserScore = null
            NullableItem<Pair<Int, Int>>(null)
        } else {
            currentMediaFilter.minUserScore = minScore
            currentMediaFilter.maxUserScore = maxScore
            NullableItem(Pair(minScore, maxScore))
        }

        _scores.onNext(scores)
    }

    fun updateStartYears(minStartYear: Int?, maxStartYear: Int?) {
        val startYears = if (minStartYear == null || maxStartYear == null) {
            currentMediaFilter.minUserStartYear = null
            currentMediaFilter.maxUserStartYear = null
            NullableItem<Pair<Int, Int>>(null)
        } else {
            currentMediaFilter.minUserStartYear = minStartYear
            currentMediaFilter.maxUserStartYear = maxStartYear
            NullableItem(Pair(minStartYear, maxStartYear))
        }

        _startYears.onNext(startYears)
    }

    fun updateCompletedYears(minCompletedYear: Int?, maxCompletedYear: Int?) {
        val completedYears = if (minCompletedYear == null || maxCompletedYear == null) {
            currentMediaFilter.minUserCompletedYear = null
            currentMediaFilter.maxUserCompletedYear = null
            NullableItem<Pair<Int, Int>>(null)
        } else {
            currentMediaFilter.minUserCompletedYear = minCompletedYear
            currentMediaFilter.maxUserCompletedYear = maxCompletedYear
            NullableItem(Pair(minCompletedYear, maxCompletedYear))
        }

        _completedYears.onNext(completedYears)
    }

    fun updatePriorities(minPriority: Int?, maxPriority: Int?) {
        val priorities = if (minPriority == null || maxPriority == null) {
            currentMediaFilter.minUserPriority = null
            currentMediaFilter.maxUserPriority = null
            NullableItem<Pair<Int, Int>>(null)
        } else {
            currentMediaFilter.minUserPriority = minPriority
            currentMediaFilter.maxUserPriority = maxPriority
            NullableItem(Pair(minPriority, maxPriority))
        }

        _priorities.onNext(priorities)
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

    fun loadReleaseYearsSliderItem() {
        val sliderItem = SliderItem(
            1950,
            getYearSliderMaxValue(),
            currentMediaFilter.minYear,
            currentMediaFilter.maxYear
        )
        _releaseYearsSliderItem.onNext(sliderItem)
    }

    fun loadEpisodesSliderItem() {
        val sliderItem = SliderItem(
            0,
            150,
            currentMediaFilter.minEpisodes,
            currentMediaFilter.maxEpisodes
        )
        _episodesSliderItem.onNext(sliderItem)
    }

    fun loadDurationsSliderItem() {
        val sliderItem = SliderItem(
            0,
            180,
            currentMediaFilter.minDuration,
            currentMediaFilter.maxDuration
        )
        _durationsSliderItem.onNext(sliderItem)
    }

    fun loadAverageScoresSliderItem() {
        val sliderItem = SliderItem(
            0,
            180,
            currentMediaFilter.minAverageScore,
            currentMediaFilter.maxAverageScore
        )
        _averageScoresSliderItem.onNext(sliderItem)
    }

    fun loadPopularitySliderItem() {
        val sliderItem = SliderItem(
            0,
            300000,
            currentMediaFilter.minPopularity,
            currentMediaFilter.maxPopularity
        )
        _popularitySliderItem.onNext(sliderItem)
    }

    fun loadUserScoresSliderItem() {
        val sliderItem = SliderItem(
            0,
            100,
            currentMediaFilter.minUserScore,
            currentMediaFilter.maxUserScore
        )
        _scoresSliderItem.onNext(sliderItem)
    }

    fun loadUserStartYearsSliderItem() {
        val sliderItem = SliderItem(
            1950,
            getYearSliderMaxValue(),
            currentMediaFilter.minUserStartYear,
            currentMediaFilter.maxUserStartYear
        )
        _startYearsSliderItem.onNext(sliderItem)
    }

    fun loadUserCompletedYearsSliderItem() {
        val sliderItem = SliderItem(
            1950,
            getYearSliderMaxValue(),
            currentMediaFilter.minUserCompletedYear,
            currentMediaFilter.maxUserCompletedYear
        )
        _completedYearsSliderItem.onNext(sliderItem)
    }

    fun loadUserPrioritiesSliderItem() {
        val sliderItem = SliderItem(
            0,
            5,
            currentMediaFilter.minUserPriority,
            currentMediaFilter.maxUserPriority
        )
        _prioritiesYearsSliderItem.onNext(sliderItem)
    }

    private fun getYearSliderMaxValue(): Int {
        return TimeUtil.getCurrentYear() + 1
    }
}