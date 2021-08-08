package com.zen.alchan.ui.medialist

import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.data.response.anilist.MediaListGroup
import com.zen.alchan.data.response.anilist.User
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

    private var rawMediaListCollection: MediaListCollection? = null
    private var currentMediaListCollection: MediaListCollection? = null

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
            _listSections.onNext(
                groups.map {
                    val formattedTitle = "${it.name} (${it.entries.size})"
                    ListItem(text = formattedTitle, data = formattedTitle)
                }
            )
        }
    }

    fun showSelectedSectionMediaList(index: Int) {
        val currentGroups = currentMediaListCollection?.lists ?: listOf()
        if (index >= 0 && index < currentGroups.size) {
            _mediaListAdapterComponent.value?.let {
                _mediaListAdapterComponent.onNext(
                    it.copy(
                        mediaListItems = currentGroups[index].entries.map { mediaList ->
                            MediaListItem(mediaList = mediaList, viewType = MediaListItem.VIEW_TYPE_MEDIA_LIST)
                        }
                    )
                )
            }
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

        list.addAll(convertMediaListGroupToMediaListItem(groupWithSortedAndFilteredEntries))

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

    private fun convertMediaListGroupToMediaListItem(groups: List<MediaListGroup>): List<MediaListItem> {
        val list = ArrayList<MediaListItem>()

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

        // use to prevent duplication
        val addedGroups = mutableSetOf<MediaListGroup>()

        sectionOrder.forEach { section ->
            val group = groups.find { it.name == section }
            if (group != null && addedGroups.add(group)) {
                list.add(MediaListItem(title = group.name, viewType = MediaListItem.VIEW_TYPE_TITLE))
                list.addAll(group.entries.map { MediaListItem(mediaList = it, viewType = MediaListItem.VIEW_TYPE_MEDIA_LIST) })
            }
        }

        customList.forEach { custom ->
            val group = groups.find { it.name == custom && it.isCustomList }
            if (group != null && addedGroups.add(group)) {
                list.add(MediaListItem(title = group.name, viewType = MediaListItem.VIEW_TYPE_TITLE))
                list.addAll(group.entries.map { MediaListItem(mediaList = it, viewType = MediaListItem.VIEW_TYPE_MEDIA_LIST) })
            }
        }

        defaultList.forEach { default ->
            val group = groups.find { it.name == default && !it.isCustomList }
            if (group != null && addedGroups.add(group)) {
                list.add(MediaListItem(title = group.name, viewType = MediaListItem.VIEW_TYPE_TITLE))
                list.addAll(group.entries.map { MediaListItem(mediaList = it, viewType = MediaListItem.VIEW_TYPE_MEDIA_LIST) })
            }
        }

        currentMediaListCollection = MediaListCollection(addedGroups.toList())

        return list
    }
}