package com.zen.alchan.ui.medialist

import com.apollographql.apollo.api.CustomTypeValue
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.pojo.MediaListAdapterComponent
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaListStatus
import type.ScoreFormat
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class MediaListViewModel(
    private val mediaListRepository: MediaListRepository,
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel() {

    private val _toolbarTitle = BehaviorSubject.createDefault(R.string.anime_list)
    val toolbarTitle: Observable<Int>
        get() = _toolbarTitle

    private val _toolbarSubtitle = BehaviorSubject.createDefault("")
    val toolbarSubtitle: Observable<String>
        get() = _toolbarSubtitle

    private val _menuItemCustomiseListVisibility = BehaviorSubject.createDefault(false)
    val menuItemCustomiseListVisibility: Observable<Boolean>
        get() = _menuItemCustomiseListVisibility

    private val _menuItemChangeListTypeVisibility = BehaviorSubject.createDefault(false)
    val menuItemChangeListTypeVisibility: Observable<Boolean>
        get() = _menuItemChangeListTypeVisibility

    private val _mediaListAdapterComponent = PublishSubject.create<MediaListAdapterComponent>()
    val mediaListAdapterComponent: Observable<MediaListAdapterComponent>
        get() = _mediaListAdapterComponent

    private val _mediaListItems = BehaviorSubject.createDefault<List<MediaListItem>>(listOf())
    val mediaListItems: Observable<List<MediaListItem>>
        get() = _mediaListItems

    private val _listSections = PublishSubject.create<List<ListItem<String>>>()
    val listSections: Observable<List<ListItem<String>>>
        get() = _listSections

    private val _scoreValues = PublishSubject.create<Pair<MediaList, ScoreFormat>>()
    val scoreValues: Observable<Pair<MediaList, ScoreFormat>>
        get() = _scoreValues

    private val _progressValues = PublishSubject.create<Pair<MediaList, Boolean>>()
    val progressValues: Observable<Pair<MediaList, Boolean>>
        get() = _progressValues

    private val _setToWatchingDialog = PublishSubject.create<Triple<MediaList, Int, Boolean>>()
    val setToWatchingDialog: Observable<Triple<MediaList, Int, Boolean>> // media list, new progress, isProgressVolume
        get() = _setToWatchingDialog

    private val _listTypes = PublishSubject.create<List<ListItem<ListType>>>()
    val listTypes: Observable<List<ListItem<ListType>>>
        get() = _listTypes

    var mediaType: MediaType = MediaType.ANIME
    var userId = 0

    var isViewer = true
    var user = User()
    var appSetting = AppSetting()
    var listStyle = ListStyle()
    var mediaFilter = MediaFilter()
    private var isAllListPositionAtTop = true

    private var rawMediaListCollection: MediaListCollection? = null // needed when applying filter
    private var currentMediaListCollection: MediaListCollection? = null // needed to show number of entries in each section
    private var currentMediaListItems: List<MediaListItem> = listOf() // needed for search

    private var selectedSectionIndex = 0
    private var searchKeyword = ""

    override fun loadData() {
        loadOnce {
            isViewer = userId == 0

            _loading.onNext(true)

            _toolbarTitle.onNext(
                when (mediaType) {
                    MediaType.ANIME -> R.string.anime_list
                    MediaType.MANGA -> R.string.manga_list
                }
            )

            disposables.add(
                userRepository.getIsAuthenticated()
                    .applyScheduler()
                    .filter { it }
                    .flatMap {
                        Observable.zip(
                            userRepository.getListStyle(MediaType.valueOf(mediaType.name)),
                            userRepository.getAppSetting(),
                            userRepository.getMediaFilter(mediaType),
                            if (isViewer) userRepository.getViewer(Source.CACHE) else browseRepository.getUser(userId),
                            browseRepository.getOthersListType()
                        ) { listStyle, appSetting, mediaFilter, user, othersListType ->
                            this.listStyle = if (isViewer) listStyle else ListStyle.getOthersListStyle(othersListType)
                            this.appSetting = appSetting
                            this.mediaFilter = if (isViewer) mediaFilter else MediaFilter(false)
                            isAllListPositionAtTop = when (mediaType) {
                                MediaType.ANIME -> appSetting.isAllAnimeListPositionAtTop
                                MediaType.MANGA -> appSetting.isAllMangaListPositionAtTop
                            }
                            return@zip user
                        }
                    }
                    .zipWith(userRepository.getListBackground(mediaType)) { user, backgroundUri ->
                        return@zipWith user to backgroundUri
                    }
                    .subscribe { (user, backgroundUri) ->
                        if (userId == 0)
                            userId = user.id

                        this.user = user

                        _menuItemCustomiseListVisibility.onNext(isViewer)
                        _menuItemChangeListTypeVisibility.onNext(!isViewer)

                        _mediaListAdapterComponent.onNext(
                            MediaListAdapterComponent(
                                isViewer,
                                listStyle,
                                appSetting,
                                user.mediaListOptions,
                                if (isViewer) backgroundUri.data else null
                            )
                        )

                        getMediaListCollection(state == State.LOADED || state == State.ERROR)
                    }
            )

            disposables.add(
                mediaListRepository.refreshMediaListTrigger
                    .applyScheduler()
                    .filter { it.first == mediaType }
                    .subscribe { (mediaType, newMediaList) ->
                        if (newMediaList == null) {
                            reloadData()
                        } else {
                            _loading.onNext(true)

                            // get all the index of the modified MediaList
                            var previousMediaList: MediaList? = null
                            val mediaListGroupIndex = ArrayList<Int>()
                            val mediaListIndex = ArrayList<Int>()
                            rawMediaListCollection?.lists?.forEachIndexed { groupIndex, mediaListGroup ->
                                mediaListGroup.entries.forEachIndexed { listIndex, mediaList ->
                                    if (mediaList.id == newMediaList.id) {
                                        mediaListGroupIndex.add(groupIndex)
                                        mediaListIndex.add(listIndex)
                                        previousMediaList = mediaList
                                    }
                                }
                            }

                            // reload if it's a new entry or when the status is changed or when the visibility is changed
                            if (previousMediaList == null ||
                                previousMediaList?.status != newMediaList.status ||
                                previousMediaList?.hiddenFromStatusLists != newMediaList.hiddenFromStatusLists
                            ) {
                                reloadData()
                                return@subscribe
                            }

                            // reload if the custom lists is changed
                            val oldCustomLists = (previousMediaList?.customLists as? CustomTypeValue<LinkedHashMap<String, Boolean>>)?.value
                            val newCustomLists = (newMediaList.customLists as? CustomTypeValue<LinkedHashMap<String, Boolean>>)?.value
                            newCustomLists?.forEach { (key, value) ->
                                if (oldCustomLists?.get(key) != value) {
                                    reloadData()
                                    return@subscribe
                                }
                            }

                            // modify the collection with the new MediaList
                            mediaListGroupIndex.zip(mediaListIndex).forEach { (groupIndex, listIndex) ->
                                rawMediaListCollection?.lists?.get(groupIndex)?.entries?.get(listIndex)?.apply {
                                    status = newMediaList.status
                                    score = newMediaList.score
                                    progress = newMediaList.progress
                                    progressVolumes = newMediaList.progressVolumes
                                    repeat = newMediaList.repeat
                                    priority = newMediaList.priority
                                    private = newMediaList.private
                                    notes = newMediaList.notes
                                    hiddenFromStatusLists = newMediaList.hiddenFromStatusLists
                                    customLists = newMediaList.customLists
                                    advancedScores = newMediaList.advancedScores
                                    startedAt = newMediaList.startedAt
                                    completedAt = newMediaList.completedAt
                                    updatedAt = newMediaList.updatedAt
                                    createdAt = newMediaList.createdAt
                                }
                            }

                            // emit the change
                            rawMediaListCollection?.let {
                                _mediaListItems.onNext(getFilteredAndSortedList(it))

                                if (searchKeyword.isNotBlank())
                                    filterByText(searchKeyword)

                                mediaListRepository.updateCacheMediaList(mediaType, it)
                            }

                            _loading.onNext(false)
                        }
                    }
            )

            disposables.add(
                userRepository.refreshMainScreenTrigger
                    .applyScheduler()
                    .subscribe {
                        state = State.INIT
                        loadData()
                    }
            )
        }
    }

    fun updateListStyle(newListStyle: ListStyle) {
        listStyle = newListStyle

        disposables.add(
            userRepository.getListBackground(mediaType)
                .applyScheduler()
                .subscribe { uri ->
                    _mediaListAdapterComponent.onNext(
                        MediaListAdapterComponent(
                            isViewer,
                            listStyle,
                            appSetting,
                            user.mediaListOptions,
                            uri.data
                        )
                    )

                    _mediaListItems.value?.let {
                        _mediaListItems.onNext(it)
                    }
                }
        )
    }

    fun updateMediaFilter(newFilter: MediaFilter) {
        mediaFilter = newFilter

        rawMediaListCollection?.let {
            val filteredAndSortedList = getFilteredAndSortedList(it)
            _mediaListItems.onNext(filteredAndSortedList)

            if (searchKeyword.isNotBlank())
                filterByText(searchKeyword)
        }
    }

    fun reloadData() {
        getMediaListCollection(true)
    }

    fun loadListSections() {
        currentMediaListCollection?.lists?.let { groups ->
            val sections = ArrayList<ListItem<String>>()
            var totalEntries = 0
            val listFromCurrentGroups = groups.map {
                totalEntries += it.entries.size
                val formattedTitle = "${it.name} (${it.entries.size})"
                ListItem(formattedTitle, formattedTitle)
            }
            sections.addAll(listFromCurrentGroups)

            val allListItem = ListItem("All ($totalEntries)", "All")
            if (isAllListPositionAtTop) {
                sections.add(0, allListItem)
            } else {
                sections.add(allListItem)
            }

            _listSections.onNext(sections)
        }
    }

    fun showSelectedSectionMediaList(index: Int) {
        val currentGroups = currentMediaListCollection?.lists ?: listOf()
        selectedSectionIndex = index
        val mediaListItems = getMediaListItems(currentGroups, index)
        _mediaListItems.onNext(mediaListItems)

        if (searchKeyword.isNotBlank())
            filterByText(searchKeyword)
    }

    fun filterByText(query: String) {
        searchKeyword = query
        val filteredMediaListItems = ArrayList<MediaListItem>()

        var isLastItemTitle = false
        currentMediaListItems.forEachIndexed { index, mediaListItem ->
            if (mediaListItem.viewType == MediaListItem.VIEW_TYPE_TITLE) {
                if (isLastItemTitle) {
                    filteredMediaListItems.removeAt(filteredMediaListItems.lastIndex)
                }
                filteredMediaListItems.add(mediaListItem)
                isLastItemTitle = true
            } else if (
                mediaListItem.mediaList.media.title.romaji.contains(query, true) ||
                mediaListItem.mediaList.media.title.english.contains(query, true) ||
                mediaListItem.mediaList.media.title.native.contains(query, true) ||
                mediaListItem.mediaList.media.synonyms.find { synonym -> synonym.contains(query, true) } != null ||
                mediaListItem.mediaList.notes.contains(query, true)
            ) {
                filteredMediaListItems.add(mediaListItem)
                isLastItemTitle = false
            } else if (index == currentMediaListItems.lastIndex && isLastItemTitle) {
                filteredMediaListItems.removeAt(filteredMediaListItems.lastIndex)
            }
        }

        _mediaListItems.onNext(filteredMediaListItems)
    }

    private fun getMediaListCollection(isReloading: Boolean = false) {
        if (isReloading)
            _loading.onNext(true)

        disposables.add(
            mediaListRepository.getMediaListCollection(Source.NETWORK, userId, mediaType)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { mediaListCollection ->
                        rawMediaListCollection = mediaListCollection
                        val filteredAndSortedList = getFilteredAndSortedList(mediaListCollection)
                        _mediaListItems.onNext(filteredAndSortedList)

                        if (searchKeyword.isNotBlank())
                            filterByText(searchKeyword)

                        state = State.LOADED
                    },
                    {
                        getMediaListCollectionFromCache()
                    }
                )
        )
    }

    /**
     * Purposely not handled in Repository because need to inform user when loaded from cache
     */
    private fun getMediaListCollectionFromCache() {
        disposables.add(
            mediaListRepository.getMediaListCollection(Source.CACHE, userId, mediaType)
                .applyScheduler()
                .subscribe(
                    {
                        rawMediaListCollection = it
                        val filteredAndSortedList = getFilteredAndSortedList(it)
                        _mediaListItems.onNext(filteredAndSortedList)

                        if (searchKeyword.isNotBlank())
                            filterByText(searchKeyword)

                        _success.onNext(
                            when (mediaType) {
                                MediaType.ANIME -> R.string.anime_list_is_loaded_from_cache
                                MediaType.MANGA -> R.string.manga_list_is_loaded_from_cache
                            }
                        )

                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    private fun getFilteredAndSortedList(mediaListCollection: MediaListCollection): List<MediaListItem> {
        val list = ArrayList<MediaListItem>()

        val groupWithSortedAndFilteredEntries = ArrayList<MediaListGroup>()
        mediaListCollection.lists.forEach { mediaListGroup ->
            val sortedEntries = getSortedEntries(mediaListGroup.entries)
            val filteredEntries = getFilteredEntries(sortedEntries)
            groupWithSortedAndFilteredEntries.add(mediaListGroup.copy(entries = filteredEntries))
        }
        val sortedGroups = getSortedGroups(groupWithSortedAndFilteredEntries)
        list.addAll(getMediaListItems(sortedGroups, selectedSectionIndex))

        return list
    }

    private fun getSortedEntries(entries: List<MediaList>): List<MediaList> {
        if (entries.isEmpty()) return listOf()

        val rowOrder = try {
            ListOrder.values().find { it.value == user.mediaListOptions.rowOrder } ?: ListOrder.TITLE
        } catch (e: IllegalArgumentException) {
            ListOrder.TITLE
        }

        val entriesSortedByTitle = entries.sortedBy { it.media.getTitle(appSetting) }
        val isDescending = mediaFilter.orderByDescending

        return when (mediaFilter.sort) {
            Sort.TITLE -> if (isDescending) entriesSortedByTitle.reversed() else entriesSortedByTitle
            Sort.SCORE -> sortUsing(entriesSortedByTitle, isDescending) { score }
            Sort.PROGRESS -> sortUsing(entriesSortedByTitle, isDescending) { progress }
            Sort.LAST_UPDATED -> sortUsing(entriesSortedByTitle, isDescending) { updatedAt }
            Sort.LAST_ADDED -> sortUsing(entriesSortedByTitle, isDescending) { id }
            Sort.START_DATE -> sortUsing(entriesSortedByTitle, isDescending) { TimeUtil.getMillisFromFuzzyDate(startedAt) }
            Sort.COMPLETED_DATE -> sortUsing(entriesSortedByTitle, isDescending) { TimeUtil.getMillisFromFuzzyDate(completedAt) }
            Sort.RELEASE_DATE -> sortUsing(entriesSortedByTitle, isDescending) { TimeUtil.getMillisFromFuzzyDate(media.startDate) }
            Sort.AVERAGE_SCORE -> sortUsing(entriesSortedByTitle, isDescending) { media.averageScore }
            Sort.POPULARITY -> sortUsing(entriesSortedByTitle, isDescending) { media.popularity }
            Sort.PRIORITY -> sortUsing(entriesSortedByTitle, isDescending) { priority }
            Sort.NEXT_AIRING -> {
                val defaultValueForNullAiringTime = if (isDescending) Int.MIN_VALUE else Int.MAX_VALUE
                sortUsing(entriesSortedByTitle, isDescending) { media.nextAiringEpisode?.timeUntilAiring ?: defaultValueForNullAiringTime }
            }
            else -> {
                when (rowOrder) {
                    ListOrder.SCORE -> sortUsing(entriesSortedByTitle, true) { score }
                    ListOrder.TITLE -> entriesSortedByTitle
                    ListOrder.LAST_UPDATED -> sortUsing(entriesSortedByTitle, true) { updatedAt }
                    ListOrder.LAST_ADDED -> sortUsing(entriesSortedByTitle, true) { id }
                }
            }
        }
    }

    private fun <T : Comparable<T>> sortUsing(unsortedList: List<MediaList>, sortByDescending: Boolean, comparison: MediaList.() -> T): List<MediaList> {
        return if (sortByDescending) {
            unsortedList.sortedByDescending { it.comparison() }
        } else {
            unsortedList.sortedBy { it.comparison() }
        }
    }


    private fun getFilteredEntries(entries: List<MediaList>): List<MediaList> {
        if (entries.isEmpty()) return listOf()

        val filterEntries = ArrayList(entries)

        if (mediaFilter.mediaFormats.isNotEmpty())
            filterEntries.removeAll { !mediaFilter.mediaFormats.contains(it.media.format) }

        if (mediaFilter.mediaStatuses.isNotEmpty())
            filterEntries.removeAll { !mediaFilter.mediaStatuses.contains(it.media.status) }

        if (mediaFilter.mediaSources.isNotEmpty())
            filterEntries.removeAll { !mediaFilter.mediaSources.contains(it.media.source) }

        if (mediaFilter.countries.isNotEmpty())
            filterEntries.removeAll { !mediaFilter.countries.map { it.iso } .contains(it.media.countryOfOrigin) }

        if (mediaFilter.mediaSeasons.isNotEmpty())
            filterEntries.removeAll { !mediaFilter.mediaSeasons.contains(it.media.season) }

        if (mediaFilter.minYear != null)
            filterEntries.removeAll { it.media.startDate?.year == null || mediaFilter.minYear!! > it.media.startDate.year }

        if (mediaFilter.maxYear != null)
            filterEntries.removeAll { it.media.startDate?.year == null || mediaFilter.maxYear!! < it.media.startDate.year }

        if (mediaFilter.minEpisodes != null) {
            filterEntries.removeAll {
                val episodes = when (mediaType) {
                    MediaType.ANIME -> it.media.episodes
                    MediaType.MANGA -> it.media.chapters
                }
                episodes == null || mediaFilter.minEpisodes!! > episodes
            }
        }

        if (mediaFilter.maxEpisodes != null) {
            filterEntries.removeAll {
                val episodes = when (mediaType) {
                    MediaType.ANIME -> it.media.episodes
                    MediaType.MANGA -> it.media.chapters
                }
                episodes == null || mediaFilter.maxEpisodes!! < episodes
            }
        }

        if (mediaFilter.minDuration != null) {
            filterEntries.removeAll {
                val durations = when (mediaType) {
                    MediaType.ANIME -> it.media.duration
                    MediaType.MANGA -> it.media.volumes
                }
                durations == null || mediaFilter.minDuration!! > durations
            }
        }

        if (mediaFilter.maxDuration != null) {
            filterEntries.removeAll {
                val durations = when (mediaType) {
                    MediaType.ANIME -> it.media.duration
                    MediaType.MANGA -> it.media.volumes
                }
                durations == null || mediaFilter.maxDuration!! < durations
            }
        }

        if (mediaFilter.minAverageScore != null)
            filterEntries.removeAll { mediaFilter.minAverageScore!! > it.media.averageScore }

        if (mediaFilter.maxAverageScore != null)
            filterEntries.removeAll { mediaFilter.maxAverageScore!! < it.media.averageScore }

        if (mediaFilter.minPopularity != null)
            filterEntries.removeAll { mediaFilter.minPopularity!! > it.media.popularity }

        if (mediaFilter.maxPopularity != null)
            filterEntries.removeAll { mediaFilter.maxPopularity!! < it.media.popularity }

        if (mediaFilter.streamingOn.isNotEmpty()) {
            filterEntries.removeAll { mediaList ->
                !mediaFilter.streamingOn
                    .map {
                        it.siteName.toLowerCase()
                    }
                    .any { siteName ->
                        mediaList.media.externalLinks
                            .map { it.site.toLowerCase() }
                            .contains(siteName)
                    }
            }
        }

        if (mediaFilter.includedGenres.isNotEmpty()) {
            filterEntries.removeAll { mediaList ->
                !mediaFilter.includedGenres
                    .map {
                        it.toLowerCase()
                    }
                    .any { genre ->
                        mediaList.media.genres
                            .map { it.name.toLowerCase() }
                            .contains(genre)
                    }
            }
        }

        if (mediaFilter.excludedGenres.isNotEmpty()) {
            filterEntries.removeAll { mediaList ->
                mediaFilter.excludedGenres
                    .map {
                        it.toLowerCase()
                    }
                    .any { genre ->
                        mediaList.media.genres
                            .map { it.name.toLowerCase() }
                            .contains(genre)
                    }
            }
        }

        if (mediaFilter.includedTags.isNotEmpty()) {
            filterEntries.removeAll { mediaList ->
                !mediaFilter.includedTags
                    .map {
                        it.id
                    }
                    .any { tag ->
                        mediaList.media.tags
                            .map { it.id }
                            .contains(tag)
                    }
            }
        }

        if (mediaFilter.excludedTags.isNotEmpty()) {
            filterEntries.removeAll { mediaList ->
                mediaFilter.excludedTags
                    .map {
                        it.id
                    }
                    .any { tag ->
                        mediaList.media.tags
                            .map { it.id }
                            .contains(tag)
                    }
            }
        }

        if (mediaFilter.minUserScore != null)
            filterEntries.removeAll { mediaFilter.minUserScore!! > it.score }

        if (mediaFilter.maxUserScore != null)
            filterEntries.removeAll { mediaFilter.maxUserScore!! < it.score }

        if (mediaFilter.minUserStartYear != null)
            filterEntries.removeAll { it.startedAt?.year == null || mediaFilter.minUserStartYear!! > it.startedAt?.year!! }

        if (mediaFilter.maxUserStartYear != null)
            filterEntries.removeAll { it.startedAt?.year == null || mediaFilter.maxUserStartYear!! < it.startedAt?.year!! }

        if (mediaFilter.minUserCompletedYear != null)
            filterEntries.removeAll { it.completedAt?.year == null || mediaFilter.minUserCompletedYear!! > it.completedAt?.year!! }

        if (mediaFilter.maxUserCompletedYear != null)
            filterEntries.removeAll { it.completedAt?.year == null || mediaFilter.maxUserCompletedYear!! < it.completedAt?.year!! }

        if (mediaFilter.minUserPriority != null) {
            filterEntries.removeAll { mediaFilter.minUserPriority!! > it.priority }
        }

        if (mediaFilter.maxUserPriority != null) {
            filterEntries.removeAll { mediaFilter.maxUserPriority!! < it.priority }
        }

        if (mediaFilter.isDoujin != null) {
            filterEntries.removeAll { mediaFilter.isDoujin == it.media.isLicensed }
        }

        return filterEntries
    }

    private fun getSortedGroups(groups: List<MediaListGroup>): List<MediaListGroup> {
        val (sectionOrder, customList, defaultList) = when (mediaType) {
            MediaType.ANIME -> {
                Triple(
                    user.mediaListOptions.animeList.sectionOrder,
                    user.mediaListOptions.animeList.customLists,
                    if (user.mediaListOptions.animeList.splitCompletedSectionByFormat)
                        mediaListRepository.defaultAnimeListSplitCompletedSectionByFormat
                    else
                        mediaListRepository.defaultAnimeList
                )
            }
            MediaType.MANGA -> {
                Triple(
                    user.mediaListOptions.mangaList.sectionOrder,
                    user.mediaListOptions.mangaList.customLists,
                    if (user.mediaListOptions.mangaList.splitCompletedSectionByFormat)
                        mediaListRepository.defaultMangaListSplitCompletedSectionByFormat
                    else
                        mediaListRepository.defaultMangaList
                )
            }
        }

        val normalizedGroups = mutableSetOf<MediaListGroup>()

        sectionOrder.forEach { section ->
            val group = groups.find { it.name == section }
            if (group != null) normalizedGroups.add(group)
        }

        customList.forEach { custom ->
            val group = groups.find { it.name == custom && it.isCustomList }
            if (group != null) { normalizedGroups.add(group) }
        }

        defaultList.forEach { default ->
            val group = groups.find { it.name == default && !it.isCustomList }
            if (group != null) normalizedGroups.add(group)
        }

        currentMediaListCollection = MediaListCollection(normalizedGroups.toList())

        return normalizedGroups.toList()
    }

    private fun getMediaListItems(groups: List<MediaListGroup>, index: Int = 0): List<MediaListItem> {
        val list = ArrayList<MediaListItem>()

        val isAllList = if (isAllListPositionAtTop) {
            index == 0
        } else {
            index == groups.size
        }

        if (isAllList) {
            groups.forEach { group ->
                if (group.entries.isNotEmpty()) {
                    list.add(MediaListItem(title = group.name, viewType = MediaListItem.VIEW_TYPE_TITLE))
                    list.addAll(group.entries.map { MediaListItem(mediaList = it, viewType = MediaListItem.VIEW_TYPE_MEDIA_LIST) })
                }
            }

            _toolbarSubtitle.onNext("All (${list.count { it.viewType == MediaListItem.VIEW_TYPE_MEDIA_LIST }})")
        } else {
            // "All" list is just for display, not actually stored
            // It does not exist in "groups"
            // That is why we should calculate the actual index without "All" list
            var selectedIndex = if (isAllListPositionAtTop) index - 1 else index
            if (selectedIndex >= groups.size)
                selectedIndex = groups.lastIndex
            list.addAll(groups[selectedIndex].entries.map { MediaListItem(mediaList = it, viewType = MediaListItem.VIEW_TYPE_MEDIA_LIST) })

            _toolbarSubtitle.onNext("${groups[selectedIndex].name} (${list.size})")
        }

        currentMediaListItems = list

        return list
    }

    fun loadScoreValues(mediaList: MediaList) {
        val scoreFormat = user.mediaListOptions.scoreFormat ?: ScoreFormat.POINT_100
        _scoreValues.onNext(mediaList to scoreFormat)
    }

    fun updateScore(mediaList: MediaList, newScore: Double, newAdvancedScores: LinkedHashMap<String, Double>?) {
        _loading.onNext(true)
        disposables.add(
            mediaListRepository.updateMediaListScore(mediaType, mediaList.id, newScore, newAdvancedScores?.map { it.value })
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        // do nothing
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun loadProgressValues(mediaList: MediaList, isProgressVolume: Boolean) {
        _progressValues.onNext(mediaList to isProgressVolume)
    }

    fun updateProgress(mediaList: MediaList, newProgress: Int, isProgressVolume: Boolean) {
        val currentProgress = when (mediaList.media.type) {
            type.MediaType.ANIME -> mediaList.progress
            type.MediaType.MANGA -> if (isProgressVolume) (mediaList.progressVolumes ?: 0) else mediaList.progress
            else -> 0
        }

        if (currentProgress == newProgress)
            return

        val maxProgress = when (mediaList.media.type) {
            type.MediaType.ANIME -> mediaList.media.episodes
            type.MediaType.MANGA -> if (isProgressVolume) mediaList.media.volumes else mediaList.media.chapters
            else -> null
        }

        var targetProgress = newProgress
        var status: MediaListStatus? = null
        var repeat: Int? = null

        if (maxProgress != null && targetProgress >= maxProgress) {
            if (mediaList.status == MediaListStatus.REPEATING)
                repeat = mediaList.repeat + 1

            status = MediaListStatus.COMPLETED
            targetProgress = maxProgress
        } else {
            if (mediaList.status == MediaListStatus.PLANNING ||
                mediaList.status == MediaListStatus.PAUSED ||
                mediaList.status == MediaListStatus.DROPPED
            ) {
                _setToWatchingDialog.onNext(Triple(mediaList, targetProgress, isProgressVolume))
                return
            }
        }

        updateProgress(mediaList.id, status, repeat, targetProgress, isProgressVolume)
    }

    fun updateProgress(mediaList: MediaList, status: MediaListStatus?, newProgress: Int, isProgressVolume: Boolean) {
        updateProgress(mediaList.id, status, null,  newProgress, isProgressVolume)
    }

    private fun updateProgress(mediaListId: Int, status: MediaListStatus?, repeat: Int?, progress: Int, isProgressVolume: Boolean) {
        _loading.onNext(true)

        disposables.add(
            mediaListRepository.updateMediaListProgress(
                mediaType,
                mediaListId,
                status,
                repeat,
                if (isProgressVolume) null else progress,
                if (isProgressVolume) progress else null
            )
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        // do nothing
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun loadListTypes() {
        _listTypes.onNext(ListType.values().map { ListItem(it.getString(), it) })
    }

    fun updateListType(newListType: ListType) {
        browseRepository.updateOthersListType(newListType)

        _mediaListAdapterComponent.onNext(
            MediaListAdapterComponent(
                isViewer,
                listStyle.copy(listType = newListType),
                appSetting,
                user.mediaListOptions,
                null
            )
        )

        _mediaListItems.value?.let {
            _mediaListItems.onNext(it)
        }
    }
}