package com.zen.alchan.ui.medialist

import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.pojo.MediaListAdapterComponent
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.customise.SharedCustomiseViewModel
import com.zen.alchan.ui.filter.SharedFilterViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*
import kotlin.collections.ArrayList

class MediaListViewModel(
    private val mediaListRepository: MediaListRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _toolbarTitle = BehaviorSubject.createDefault(R.string.anime_list)
    val toolbarTitle: Observable<Int>
        get() = _toolbarTitle

    private val _toolbarSubtitle = BehaviorSubject.createDefault("")
    val toolbarSubtitle: Observable<String>
        get() = _toolbarSubtitle

    private val _mediaListAdapterComponent = BehaviorSubject.createDefault(MediaListAdapterComponent())
    val mediaListAdapterComponent: Observable<MediaListAdapterComponent>
        get() = _mediaListAdapterComponent

    private val _listStyle = BehaviorSubject.createDefault(ListStyle())
    val listStyle: Observable<ListStyle>
        get() = _listStyle

    private val _listSections = PublishSubject.create<List<ListItem<String>>>()
    val listSections: Observable<List<ListItem<String>>>
        get() = _listSections

    private val _listStyleAndCustomisedList = PublishSubject.create<Pair<ListStyle, SharedCustomiseViewModel.CustomisedList>>()
    val listStyleAndCustomisedList: Observable<Pair<ListStyle, SharedCustomiseViewModel.CustomisedList>>
        get() = _listStyleAndCustomisedList

    private val _mediaFilterAndFilterList = PublishSubject.create<Pair<MediaFilter, SharedFilterViewModel.FilteredList>>()
    val mediaFilterAndFilteredList: Observable<Pair<MediaFilter, SharedFilterViewModel.FilteredList>>
        get() = _mediaFilterAndFilterList

    var mediaType: MediaType = MediaType.ANIME
    var userId = 0

    private var user = User()
    private var appSetting = AppSetting()
    private var currentMediaFilter = MediaFilter()
    private var isAllListPositionAtTop = true

    private var rawMediaListCollection: MediaListCollection? = null // needed when applying filter
    private var currentMediaListCollection: MediaListCollection? = null // needed to show number of entries in each section
    private var currentMediaListItems: List<MediaListItem> = listOf() // needed for search

    private var selectedSectionIndex = 0
    private var searchKeyword = ""

    override fun loadData() {
        loadOnce {
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
                            userRepository.getViewer(Source.CACHE)
                        ) { listStyle, appSetting, mediaFilter, user ->
                            this.appSetting = appSetting
                            this.currentMediaFilter = mediaFilter
                            isAllListPositionAtTop = when (mediaType) {
                                MediaType.ANIME -> appSetting.isAllAnimeListPositionAtTop
                                MediaType.MANGA -> appSetting.isAllMangaListPositionAtTop
                            }
                            return@zip Pair(listStyle, user)
                        }
                    }
                    .subscribe { (listStyle, user) ->
                        if (userId == 0)
                            userId = user.id

                        this.user = user
                        _listStyle.onNext(listStyle)

                        getMediaListCollection(state == State.LOADED || state == State.ERROR)
                    }
            )
        }
    }

    fun loadListStyle() {
        val customisedList = when (mediaType) {
            MediaType.ANIME -> SharedCustomiseViewModel.CustomisedList.ANIME_LIST
            MediaType.MANGA -> SharedCustomiseViewModel.CustomisedList.MANGA_LIST
        }
        _listStyleAndCustomisedList.onNext((_listStyle.value ?: ListStyle()) to customisedList)
    }

    fun updateListStyle(newListStyle: ListStyle) {
        _listStyle.onNext(newListStyle)
        _mediaListAdapterComponent.value?.let {
            _mediaListAdapterComponent.onNext(it.copy(listStyle = newListStyle))
        }
    }

    fun loadMediaFilter() {
        val filterList = when (mediaType) {
            MediaType.ANIME -> SharedFilterViewModel.FilteredList.ANIME_MEDIA_LIST
            MediaType.MANGA -> SharedFilterViewModel.FilteredList.MANGA_MEDIA_LIST
        }
        _mediaFilterAndFilterList.onNext(currentMediaFilter to filterList)
    }

    fun updateMediaFilter(newFilter: MediaFilter) {
        currentMediaFilter = newFilter

        rawMediaListCollection?.let {
            val filteredAndSortedList = getFilteredAndSortedList(it)
            _mediaListAdapterComponent.value?.let {
                _mediaListAdapterComponent.onNext(it.copy(mediaListItems = filteredAndSortedList))
            }

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
        _mediaListAdapterComponent.value?.let {
            _mediaListAdapterComponent.onNext(it.copy(mediaListItems = mediaListItems))
        }

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

        _mediaListAdapterComponent.value?.let {
            _mediaListAdapterComponent.onNext(it.copy(mediaListItems = filteredMediaListItems))
        }
    }

    private fun getMediaListCollection(isReloading: Boolean = false) {
        if (isReloading)
            _loading.onNext(true)

        disposables.add(
            mediaListRepository.getMediaListCollection(userId, mediaType.getAniListMediaType())
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { mediaListCollection ->
                        rawMediaListCollection = mediaListCollection
                        val filteredAndSortedList = getFilteredAndSortedList(mediaListCollection)
                        _mediaListAdapterComponent.onNext(MediaListAdapterComponent(
                            _listStyle.value ?: ListStyle(),
                            appSetting,
                            user.mediaListOptions,
                            filteredAndSortedList
                        ))

                        if (searchKeyword.isNotBlank())
                            filterByText(searchKeyword)

                        state = State.LOADED
                    },
                    {
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

        // TODO: should sort by shown title
        val entriesSortedByTitle = entries.sortedBy { it.media.title.userPreferred.toLowerCase(Locale.getDefault()) }
        val isDescending = currentMediaFilter.orderByDescending

        return when (currentMediaFilter.sort) {
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

        if (currentMediaFilter.mediaFormats.isNotEmpty())
            filterEntries.removeAll { !currentMediaFilter.mediaFormats.contains(it.media.format) }

        if (currentMediaFilter.mediaStatuses.isNotEmpty())
            filterEntries.removeAll { !currentMediaFilter.mediaStatuses.contains(it.media.status) }

        if (currentMediaFilter.mediaSources.isNotEmpty())
            filterEntries.removeAll { !currentMediaFilter.mediaSources.contains(it.media.source) }

        if (currentMediaFilter.countries.isNotEmpty())
            filterEntries.removeAll { !currentMediaFilter.countries.map { it.iso } .contains(it.media.countryOfOrigin) }

        if (currentMediaFilter.mediaSeasons.isNotEmpty())
            filterEntries.removeAll { !currentMediaFilter.mediaSeasons.contains(it.media.season) }

        if (currentMediaFilter.minYear != null)
            filterEntries.removeAll { it.media.startDate?.year == null || currentMediaFilter.minYear!! > it.media.startDate.year }

        if (currentMediaFilter.maxYear != null)
            filterEntries.removeAll { it.media.startDate?.year == null || currentMediaFilter.maxYear!! < it.media.startDate.year }

        if (currentMediaFilter.minEpisodes != null) {
            filterEntries.removeAll {
                val episodes = when (mediaType) {
                    MediaType.ANIME -> it.media.episodes
                    MediaType.MANGA -> it.media.chapters
                }
                episodes == null || currentMediaFilter.minEpisodes!! > episodes
            }
        }

        if (currentMediaFilter.maxEpisodes != null) {
            filterEntries.removeAll {
                val episodes = when (mediaType) {
                    MediaType.ANIME -> it.media.episodes
                    MediaType.MANGA -> it.media.chapters
                }
                episodes == null || currentMediaFilter.maxEpisodes!! < episodes
            }
        }

        if (currentMediaFilter.minDuration != null) {
            filterEntries.removeAll {
                val durations = when (mediaType) {
                    MediaType.ANIME -> it.media.duration
                    MediaType.MANGA -> it.media.volumes
                }
                durations == null || currentMediaFilter.minDuration!! > durations
            }
        }

        if (currentMediaFilter.maxDuration != null) {
            filterEntries.removeAll {
                val durations = when (mediaType) {
                    MediaType.ANIME -> it.media.duration
                    MediaType.MANGA -> it.media.volumes
                }
                durations == null || currentMediaFilter.maxDuration!! < durations
            }
        }

        if (currentMediaFilter.minAverageScore != null)
            filterEntries.removeAll { currentMediaFilter.minAverageScore!! > it.media.averageScore }

        if (currentMediaFilter.maxAverageScore != null)
            filterEntries.removeAll { currentMediaFilter.maxAverageScore!! < it.media.averageScore }

        if (currentMediaFilter.minPopularity != null)
            filterEntries.removeAll { currentMediaFilter.minPopularity!! > it.media.popularity }

        if (currentMediaFilter.maxPopularity != null)
            filterEntries.removeAll { currentMediaFilter.maxPopularity!! < it.media.popularity }

        if (currentMediaFilter.streamingOn.isNotEmpty()) {
            filterEntries.removeAll { mediaList ->
                !currentMediaFilter.streamingOn
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

        if (currentMediaFilter.includedGenres.isNotEmpty()) {
            filterEntries.removeAll { mediaList ->
                !currentMediaFilter.includedGenres
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

        if (currentMediaFilter.excludedGenres.isNotEmpty()) {
            filterEntries.removeAll { mediaList ->
                currentMediaFilter.excludedGenres
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

        if (currentMediaFilter.includedTags.isNotEmpty()) {
            filterEntries.removeAll { mediaList ->
                !currentMediaFilter.includedTags
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

        if (currentMediaFilter.excludedTags.isNotEmpty()) {
            filterEntries.removeAll { mediaList ->
                currentMediaFilter.excludedTags
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

        if (currentMediaFilter.minUserScore != null)
            filterEntries.removeAll { currentMediaFilter.minUserScore!! > it.score }

        if (currentMediaFilter.maxUserScore != null)
            filterEntries.removeAll { currentMediaFilter.maxUserScore!! < it.score }

        if (currentMediaFilter.minUserStartYear != null)
            filterEntries.removeAll { it.startedAt?.year == null || currentMediaFilter.minUserStartYear!! > it.startedAt.year }

        if (currentMediaFilter.maxUserStartYear != null)
            filterEntries.removeAll { it.startedAt?.year == null || currentMediaFilter.maxUserStartYear!! < it.startedAt.year }

        if (currentMediaFilter.minUserCompletedYear != null)
            filterEntries.removeAll { it.completedAt?.year == null || currentMediaFilter.minUserCompletedYear!! > it.completedAt.year }

        if (currentMediaFilter.maxUserCompletedYear != null)
            filterEntries.removeAll { it.completedAt?.year == null || currentMediaFilter.maxUserCompletedYear!! < it.completedAt.year }

        if (currentMediaFilter.minUserPriority != null) {
            filterEntries.removeAll { currentMediaFilter.minUserPriority!! > it.priority }
        }

        if (currentMediaFilter.maxUserPriority != null) {
            filterEntries.removeAll { currentMediaFilter.maxUserPriority!! < it.priority }
        }

        if (currentMediaFilter.isDoujin != null) {
            filterEntries.removeAll { currentMediaFilter.isDoujin == it.media.isLicensed }
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
}