package com.zen.alchan.ui.medialist

import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.ListOrder
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.enums.getAniListMediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.pojo.MediaListAdapterComponent
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.ui.base.BaseViewModel
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

    private val _mediaListAdapterComponent = BehaviorSubject.createDefault(MediaListAdapterComponent.EMPTY_MEDIA_LIST_ADAPTER_COMPONENT)
    val mediaListAdapterComponent: Observable<MediaListAdapterComponent>
        get() = _mediaListAdapterComponent

    private val _listStyle = BehaviorSubject.createDefault(ListStyle.EMPTY_LIST_STYLE)
    val listStyle: Observable<ListStyle>
        get() = _listStyle

    private val _listSections = PublishSubject.create<List<ListItem<String>>>()
    val listSections: Observable<List<ListItem<String>>>
        get() = _listSections

    var mediaType: MediaType = MediaType.ANIME
    var userId = 0

    private var user = User.EMPTY_USER
    private var appSetting = AppSetting.EMPTY_APP_SETTING
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
                            userRepository.getViewer(Source.CACHE)
                        ) { listStyle, appSetting, user ->
                            return@zip Triple(listStyle, appSetting, user)
                        }
                    }
                    .subscribe { (listStyle, appSetting, user) ->
                        if (userId == 0) {
                            userId = user.id
                        }

                        this.user = user
                        this.appSetting = appSetting
                        _listStyle.onNext(listStyle)

                        isAllListPositionAtTop = when (mediaType) {
                            MediaType.ANIME -> appSetting.isAllAnimeListPositionAtTop
                            MediaType.MANGA -> appSetting.isAllMangaListPositionAtTop
                        }

                        getMediaListCollection(state == State.LOADED || state == State.ERROR)
                    }
            )
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
                ListItem(text = formattedTitle, data = formattedTitle)
            }
            sections.addAll(listFromCurrentGroups)

            val allListItem = ListItem(text = "All ($totalEntries)", data = "All")
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
                            _listStyle.value ?: ListStyle.EMPTY_LIST_STYLE,
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

        val entriesSortedByTitle = entries.sortedBy { it.media.title.userPreferred.toLowerCase(Locale.getDefault()) }

        // TODO: implement more sort later
        return when (rowOrder) {
            ListOrder.SCORE -> entriesSortedByTitle.sortedByDescending { it.score }
            ListOrder.TITLE -> entriesSortedByTitle
            ListOrder.LAST_UPDATED -> entriesSortedByTitle.sortedByDescending { it.updatedAt }
            ListOrder.LAST_ADDED -> entriesSortedByTitle.sortedByDescending { it.id }
        }
    }

    private fun getFilteredEntries(entries: List<MediaList>): List<MediaList> {
        if (entries.isEmpty()) return listOf()

        // TODO: implement filter later
        return entries
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
                list.add(MediaListItem(title = group.name, viewType = MediaListItem.VIEW_TYPE_TITLE))
                list.addAll(group.entries.map { MediaListItem(mediaList = it, viewType = MediaListItem.VIEW_TYPE_MEDIA_LIST) })
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