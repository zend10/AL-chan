package com.zen.alchan.ui.filter

import com.zen.alchan.R
import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.MediaTag
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

class FilterViewModel(private val contentRepository: ContentRepository) : BaseViewModel() {

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

    private val _includedGenresLayoutVisibility = BehaviorSubject.createDefault(false)
    val includedGenresLayoutVisibility: Observable<Boolean>
        get() = _includedGenresLayoutVisibility

    private val _excludedGenresLayoutVisibility = BehaviorSubject.createDefault(false)
    val excludedGenresLayoutVisibility: Observable<Boolean>
        get() = _excludedGenresLayoutVisibility

    private val _noItemGenreLayoutVisibility = BehaviorSubject.createDefault(false)
    val noItemGenreLayoutVisibility: Observable<Boolean>
        get() = _noItemGenreLayoutVisibility

    private val _includedTagsLayoutVisibility = BehaviorSubject.createDefault(false)
    val includedTagsLayoutVisibility: Observable<Boolean>
        get() = _includedTagsLayoutVisibility

    private val _excludedTagsLayoutVisibility = BehaviorSubject.createDefault(false)
    val excludedTagsLayoutVisibility: Observable<Boolean>
        get() = _excludedTagsLayoutVisibility

    private val _noItemTagLayoutVisibility = BehaviorSubject.createDefault(false)
    val noItemTagLayoutVisibility: Observable<Boolean>
        get() = _noItemTagLayoutVisibility





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

    private val _includedGenreList = PublishSubject.create<Pair<List<ListItem<String>>, ArrayList<Int>>>()
    val includedGenreList: Observable<Pair<List<ListItem<String>>, ArrayList<Int>>>
        get() = _includedGenreList

    private val _excludedGenreList = PublishSubject.create<Pair<List<ListItem<String>>, ArrayList<Int>>>()
    val excludedGenreList: Observable<Pair<List<ListItem<String>>, ArrayList<Int>>>
        get() = _excludedGenreList

    private val _includedTagList = PublishSubject.create<Pair<List<ListItem<MediaTag?>>, ArrayList<Int>>>()
    val includedTagList: Observable<Pair<List<ListItem<MediaTag?>>, ArrayList<Int>>>
        get() = _includedTagList

    private val _excludedTagList = PublishSubject.create<Pair<List<ListItem<MediaTag?>>, ArrayList<Int>>>()
    val excludedTagList: Observable<Pair<List<ListItem<MediaTag?>>, ArrayList<Int>>>
        get() = _excludedTagList

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

    private var genres: List<Genre> = listOf()
    private var tags: List<MediaTag> = listOf()
    private var tagList: List<ListItem<MediaTag?>> = listOf()
    private var currentMediaFilter = MediaFilter.EMPTY_MEDIA_FILTER

    override fun loadData() {
        loadOnce {
            disposables.add(
                contentRepository.getGenres()
                    .subscribe {
                        genres = it
                    }
            )

            disposables.add(
                contentRepository.getTags()
                    .subscribe {
                        tags = it

                        val tagMap = tags.groupBy { tag -> tag.category }
                        val tagList = ArrayList<ListItem<MediaTag?>>()
                        tagMap.forEach { (category, tags) ->
                            tagList.add(ListItem(category, listOf(), null))
                            tagList.addAll(tags.map { tag -> ListItem(tag.name, listOf(), tag) })
                        }
                        this.tagList = tagList
                    }
            )

            handleGenreLayoutVisibility()
            handleTagLayoutVisibility()
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

    fun updateIncludedGenres(newGenres: List<String>) {
        currentMediaFilter.includedGenres = newGenres
        _includedGenres.onNext(newGenres)

        val excludedGenres = ArrayList(_excludedGenres.value ?: listOf())
        excludedGenres.removeAll(newGenres)
        _excludedGenres.onNext(excludedGenres)

        handleGenreLayoutVisibility()
    }

    fun removeIncludedGenre(index: Int) {
        val currentGenres = ArrayList(_includedGenres.value ?: listOf())
        currentGenres.removeAt(index)
        currentMediaFilter.includedGenres = currentGenres
        _includedGenres.onNext(currentGenres)

        handleGenreLayoutVisibility()
    }

    fun updateExcludedGenres(newGenres: List<String>) {
        currentMediaFilter.excludedGenres = newGenres
        _excludedGenres.onNext(newGenres)

        val includedGenres = ArrayList(_includedGenres.value ?: listOf())
        includedGenres.removeAll(newGenres)
        _includedGenres.onNext(includedGenres)

        handleGenreLayoutVisibility()
    }

    fun removeExcludedGenre(index: Int) {
        val currentGenres = ArrayList(_excludedGenres.value ?: listOf())
        currentGenres.removeAt(index)
        currentMediaFilter.excludedGenres = currentGenres
        _excludedGenres.onNext(currentGenres)

        handleGenreLayoutVisibility()
    }

    private fun handleGenreLayoutVisibility() {
        _includedGenresLayoutVisibility.onNext(!_includedGenres.value.isNullOrEmpty())
        _excludedGenresLayoutVisibility.onNext(!_excludedGenres.value.isNullOrEmpty())
        _noItemGenreLayoutVisibility.onNext(_includedGenres.value.isNullOrEmpty() && _excludedGenres.value.isNullOrEmpty())
    }

    fun updateIncludedTags(newTags: List<MediaTag>) {
        val tagNames = newTags.map { it.name }
        currentMediaFilter.includedTags = tagNames
        _includedTags.onNext(tagNames)

        val excludedTags = ArrayList(_excludedTags.value ?: listOf())
        excludedTags.removeAll(tagNames)
        _excludedTags.onNext(excludedTags)

        handleTagLayoutVisibility()
    }

    fun removeIncludedTag(index: Int) {
        val currentTags = ArrayList(_includedTags.value ?: listOf())
        currentTags.removeAt(index)
        currentMediaFilter.includedTags = currentTags
        _includedTags.onNext(currentTags)

        handleTagLayoutVisibility()
    }

    fun updateExcludedTags(newTags: List<MediaTag>) {
        val tagNames = newTags.map { it.name }
        currentMediaFilter.excludedTags = tagNames
        _excludedTags.onNext(tagNames)

        val includedTags = ArrayList(_includedTags.value ?: listOf())
        includedTags.removeAll(tagNames)
        _includedTags.onNext(includedTags)

        handleTagLayoutVisibility()
    }

    fun removeExcludedTag(index: Int) {
        val currentTags = ArrayList(_excludedTags.value ?: listOf())
        currentTags.removeAt(index)
        currentMediaFilter.excludedTags = currentTags
        _excludedTags.onNext(currentTags)

        handleTagLayoutVisibility()
    }

    private fun handleTagLayoutVisibility() {
        _includedTagsLayoutVisibility.onNext(!_includedTags.value.isNullOrEmpty())
        _excludedTagsLayoutVisibility.onNext(!_excludedTags.value.isNullOrEmpty())
        _noItemTagLayoutVisibility.onNext(_includedTags.value.isNullOrEmpty() && _excludedTags.value.isNullOrEmpty())
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
        val releaseYears = _releaseYears.value?.data
        val sliderItem = SliderItem(
            1950,
            getYearSliderMaxValue(),
            releaseYears?.first,
            releaseYears?.second
        )
        _releaseYearsSliderItem.onNext(sliderItem)
    }

    fun loadIncludedGenres() {
        val genreList = genres.map { ListItem(it.name, listOf(), it.name) }
        val selectedIndex = ArrayList<Int>()
        _includedGenres.value?.forEach {
            val index = genres.indexOfFirst { genre -> genre.name == it }
            if (index != -1)
                selectedIndex.add(index)
        }

        _includedGenreList.onNext(genreList to selectedIndex)
    }

    fun loadExcludedGenres() {
        val genreList = genres.map { ListItem(it.name, listOf(), it.name) }
        val selectedIndex = ArrayList<Int>()
        _excludedGenres.value?.forEach {
            val index = genres.indexOfFirst { genre -> genre.name == it }
            if (index != -1)
                selectedIndex.add(index)
        }

        _excludedGenreList.onNext(genreList to selectedIndex)
    }

    fun loadIncludedTags() {
        val selectedTagIds = ArrayList<Int>()
        _includedTags.value?.forEach {
            val tag = tagList.find { tag -> tag.data?.name == it }?.data
            tag?.let {
                selectedTagIds.add(tag.id)
            }
        }

        _includedTagList.onNext(tagList to selectedTagIds)
    }

    fun loadExcludedTags() {
        val selectedTagIds = ArrayList<Int>()
        _excludedTags.value?.forEach {
            val tag = tagList.find { tag -> tag.data?.name == it }?.data
            tag?.let {
                selectedTagIds.add(tag.id)
            }
        }

        _excludedTagList.onNext(tagList to selectedTagIds)
    }

    fun loadEpisodesSliderItem() {
        val episodes = _releaseYears.value?.data
        val sliderItem = SliderItem(
            0,
            150,
            episodes?.first,
            episodes?.second
        )
        _episodesSliderItem.onNext(sliderItem)
    }

    fun loadDurationsSliderItem() {
        val durations = _durations.value?.data
        val sliderItem = SliderItem(
            0,
            180,
            durations?.first,
            durations?.second
        )
        _durationsSliderItem.onNext(sliderItem)
    }

    fun loadAverageScoresSliderItem() {
        val averageScores = _averageScores.value?.data
        val sliderItem = SliderItem(
            0,
            180,
            averageScores?.first,
            averageScores?.second
        )
        _averageScoresSliderItem.onNext(sliderItem)
    }

    fun loadPopularitySliderItem() {
        val popularity = _popularity.value?.data
        val sliderItem = SliderItem(
            0,
            300000,
            popularity?.first,
            popularity?.second
        )
        _popularitySliderItem.onNext(sliderItem)
    }

    fun loadUserScoresSliderItem() {
        val scores = _scores.value?.data
        val sliderItem = SliderItem(
            0,
            100,
            scores?.first,
            scores?.second
        )
        _scoresSliderItem.onNext(sliderItem)
    }

    fun loadUserStartYearsSliderItem() {
        val startYears = _startYears.value?.data
        val sliderItem = SliderItem(
            1950,
            getYearSliderMaxValue(),
            startYears?.first,
            startYears?.second
        )
        _startYearsSliderItem.onNext(sliderItem)
    }

    fun loadUserCompletedYearsSliderItem() {
        val completedYears = _completedYears.value?.data
        val sliderItem = SliderItem(
            1950,
            getYearSliderMaxValue(),
            completedYears?.first,
            completedYears?.second
        )
        _completedYearsSliderItem.onNext(sliderItem)
    }

    fun loadUserPrioritiesSliderItem() {
        val priorities = _priorities.value?.data
        val sliderItem = SliderItem(
            0,
            5,
            priorities?.first,
            priorities?.second
        )
        _prioritiesYearsSliderItem.onNext(sliderItem)
    }

    private fun getYearSliderMaxValue(): Int {
        return TimeUtil.getCurrentYear() + 1
    }
}