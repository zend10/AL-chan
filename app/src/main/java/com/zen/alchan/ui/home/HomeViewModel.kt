package com.zen.alchan.ui.home

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.HomeAdapterComponent
import com.zen.alchan.helper.pojo.HomeItem
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.ReleasingTodayItem
import com.zen.alchan.type.MediaListStatus
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlin.math.abs

class HomeViewModel(
    private val contentRepository: ContentRepository,
    private val userRepository: UserRepository,
    private val mediaListRepository: MediaListRepository
) : BaseViewModel<Unit>() {

    private var homeAppSetting: AppSetting = AppSetting()

    private val _homeItemList = BehaviorSubject.createDefault(listOf<HomeItem>())
    val homeItemList: Observable<List<HomeItem>>
        get() = _homeItemList

    private val _adapterComponent = PublishSubject.create<HomeAdapterComponent>()
    val adapterComponent: Observable<HomeAdapterComponent>
        get() = _adapterComponent

    private val _searchCategoryList = PublishSubject.create<List<ListItem<SearchCategory>>>()
    val searchCategoryList: Observable<List<ListItem<SearchCategory>>>
        get() = _searchCategoryList

    private val _exploreCategoryList = PublishSubject.create<List<ListItem<SearchCategory>>>()
    val exploreCategoryList: Observable<List<ListItem<SearchCategory>>>
        get() = _exploreCategoryList

    override fun loadData(param: Unit) {
        // BaseFragment re-enters setUpObserver when returning to this screen.
        // Refresh saved home preferences after the first successful home load.
        if (state == State.LOADED) refreshHomeSettings()
        loadOnce {
            disposables.add(
                mediaListRepository.refreshMediaListTrigger
                    .filter { it.first == MediaType.ANIME }
                    .applyScheduler()
                    .subscribe { (_, newMediaList) ->
                        if (newMediaList == null) {
                            // A deleted entry has no payload; fetch fresh data rather than trusting stale cache.
                            loadContinueWatching(preferNetwork = true)
                        } else {
                            // Progress/status updates carry the updated entry; refresh the card immediately.
                            updateContinueWatchingEntry(newMediaList)
                        }
                    }
            )

            disposables.add(
                userRepository.getAppSetting()
                    .zipWith(userRepository.getViewer(Source.CACHE)) { appSetting, user ->
                        HomeAdapterComponent(user, appSetting)
                    }
                    .applyScheduler()
                    .subscribe {
                        homeAppSetting = it.appSetting
                        _adapterComponent.onNext(it)
                        getHomeData()
                        loadContinueWatching()
                    }
            )

            disposables.add(
                mediaListRepository.releasingTodayTrigger
                    .applyScheduler()
                    .subscribe {
                        if (homeAppSetting.showHomeReleasingToday == false) return@subscribe
                        disposables.add(
                            userRepository.getViewer(Source.CACHE)
                                .flatMap { user ->
                                    mediaListRepository.hasBigList(user, MediaType.ANIME)
                                        .map {
                                            user to it
                                        }
                                }
                                .filter {
                                    if (it.second) {
                                        val currentHomeList = ArrayList(_homeItemList.value ?: listOf())
                                        val index = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_RELEASING_TODAY }
                                        if (index != -1) {
                                            currentHomeList.removeAt(index)
                                        }
                                        _homeItemList.onNext(currentHomeList)
                                    }
                                    !it.second
                                }
                                .map {
                                    it.first
                                }
                                .flatMap {
                                    mediaListRepository.getMediaListCollection(Source.CACHE, it, MediaType.ANIME)
                                }
                                .map {
                                    it.lists.filter { it.status != MediaListStatus.DROPPED }
                                }
                                .map {
                                    val releasingTodayItem = mutableSetOf<ReleasingTodayItem>()

                                    it.forEach {
                                        it.entries.forEach { mediaList ->
                                            val media = mediaList.media
                                            var currentEpisode = 0

                                            if (media.nextAiringEpisode != null) {
                                                if (media.nextAiringEpisode.timeUntilAiring < 3600 * 24) {
                                                    releasingTodayItem.add(ReleasingTodayItem(mediaList, media.nextAiringEpisode.episode, media.nextAiringEpisode.timeUntilAiring))
                                                } else {
                                                    currentEpisode = media.nextAiringEpisode.episode - 1
                                                }
                                            }

                                            if (media.airingSchedule.nodes.isNotEmpty()) {
                                                val currentEpisodeSchedule = media.airingSchedule.nodes.find { it.episode == currentEpisode }
                                                if (currentEpisodeSchedule != null && abs(currentEpisodeSchedule.timeUntilAiring) < 3600 * 24) {
                                                    releasingTodayItem.add(ReleasingTodayItem(mediaList, currentEpisodeSchedule.episode, currentEpisodeSchedule.timeUntilAiring))
                                                }
                                            }
                                        }
                                    }

                                    releasingTodayItem.sortedBy { it.timeUntilAiring }
                                }
                                .subscribe(
                                    {
                                        val currentHomeList = ArrayList(_homeItemList.value ?: listOf())
                                        val releasingTodayIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_RELEASING_TODAY }
                                        if (releasingTodayIndex != - 1) {
                                            currentHomeList.removeAt(releasingTodayIndex)
                                        }
                                        val menuIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_MENU }
                                        val continueWatchingIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_CONTINUE_WATCHING }
                                        if (menuIndex != -1 && homeAppSetting.showHomeReleasingToday != false) {
                                            val insertIndex = if (continueWatchingIndex != -1) continueWatchingIndex + 1 else menuIndex + 1
                                            currentHomeList.add(insertIndex.coerceAtMost(currentHomeList.size), HomeItem(releasingToday = it, viewType = HomeItem.VIEW_TYPE_RELEASING_TODAY))
                                        }
                                        _homeItemList.onNext(currentHomeList)
                                    },
                                    {
                                        it.printStackTrace()
                                    }
                                )
                        )
                    }
            )
        }
    }

    private fun refreshHomeSettings() {
        disposables.add(
            userRepository.getAppSetting()
                .zipWith(userRepository.getViewer(Source.CACHE)) { appSetting, user ->
                    HomeAdapterComponent(user, appSetting)
                }
                .applyScheduler()
                .subscribe(
                    { component ->
                        val previous = homeAppSetting
                        val updated = component.appSetting
                        val releasingChanged = (previous.showHomeReleasingToday != false) != (updated.showHomeReleasingToday != false)
                        val continueChanged = (previous.showHomeContinueWatching != false) != (updated.showHomeContinueWatching != false)
                        val trendingAnimeChanged = (previous.showHomeTrendingAnime != false) != (updated.showHomeTrendingAnime != false)
                        val trendingMangaChanged = (previous.showHomeTrendingManga != false) != (updated.showHomeTrendingManga != false)

                        homeAppSetting = updated
                        _adapterComponent.onNext(component)
                        // HomeFragment replaces its adapter when adapterComponent emits.
                        // Re-emit the current list so the new adapter receives its items even
                        // when the saved section preferences have not changed.
                        _homeItemList.onNext(_homeItemList.value ?: listOf())

                        if (releasingChanged || continueChanged || trendingAnimeChanged || trendingMangaChanged) {
                            synchronizeHomeSections(
                                releasingChanged,
                                continueChanged,
                                trendingAnimeChanged,
                                trendingMangaChanged
                            )
                        }
                    },
                    { it.printStackTrace() }
                )
        )
    }

    private fun synchronizeHomeSections(
        releasingChanged: Boolean,
        continueChanged: Boolean,
        trendingAnimeChanged: Boolean,
        trendingMangaChanged: Boolean
    ) {
        val existingItems = _homeItemList.value ?: listOf()
        if (existingItems.isEmpty()) return

        val header = existingItems.firstOrNull { it.viewType == HomeItem.VIEW_TYPE_HEADER }
            ?: HomeItem(viewType = HomeItem.VIEW_TYPE_HEADER)
        val menu = existingItems.firstOrNull { it.viewType == HomeItem.VIEW_TYPE_MENU }
            ?: HomeItem(viewType = HomeItem.VIEW_TYPE_MENU)
        val releasing = existingItems.firstOrNull { it.viewType == HomeItem.VIEW_TYPE_RELEASING_TODAY }
            ?: HomeItem(viewType = HomeItem.VIEW_TYPE_RELEASING_TODAY)
        val continuing = existingItems.firstOrNull { it.viewType == HomeItem.VIEW_TYPE_CONTINUE_WATCHING }
            ?: HomeItem(viewType = HomeItem.VIEW_TYPE_CONTINUE_WATCHING)
        val trendingAnime = existingItems.firstOrNull { it.viewType == HomeItem.VIEW_TYPE_TRENDING_ANIME }
            ?: HomeItem(viewType = HomeItem.VIEW_TYPE_TRENDING_ANIME)
        val trendingManga = existingItems.firstOrNull { it.viewType == HomeItem.VIEW_TYPE_TRENDING_MANGA }
            ?: HomeItem(viewType = HomeItem.VIEW_TYPE_TRENDING_MANGA)
        val knownSections = setOf(
            HomeItem.VIEW_TYPE_HEADER,
            HomeItem.VIEW_TYPE_MENU,
            HomeItem.VIEW_TYPE_RELEASING_TODAY,
            HomeItem.VIEW_TYPE_CONTINUE_WATCHING,
            HomeItem.VIEW_TYPE_TRENDING_ANIME,
            HomeItem.VIEW_TYPE_TRENDING_MANGA
        )
        val otherItems = existingItems.filterNot { it.viewType in knownSections }
        val nextItems = mutableListOf(header, menu)
        if (homeAppSetting.showHomeContinueWatching != false) nextItems.add(continuing)
        if (homeAppSetting.showHomeReleasingToday != false) nextItems.add(releasing)
        if (homeAppSetting.showHomeTrendingAnime != false) nextItems.add(trendingAnime)
        if (homeAppSetting.showHomeTrendingManga != false) nextItems.add(trendingManga)
        nextItems.addAll(otherItems)
        _homeItemList.onNext(nextItems)

        if (releasingChanged && homeAppSetting.showHomeReleasingToday != false) {
            mediaListRepository.triggerReleasingToday()
        }
        if (continueChanged && homeAppSetting.showHomeContinueWatching != false) {
            loadContinueWatching()
        }
        if ((trendingAnimeChanged && homeAppSetting.showHomeTrendingAnime != false) ||
            (trendingMangaChanged && homeAppSetting.showHomeTrendingManga != false)) {
            requestHomeData(null)
        }
    }

    fun reloadData() {
        getHomeData(true)
    }

    private fun getHomeData(isReloading: Boolean = false) {
        if (!isReloading && state == State.LOADED) return

        if (isReloading) {
            _loading.onNext(true)
        } else {
            val initialItems = mutableListOf(
                HomeItem(viewType = HomeItem.VIEW_TYPE_HEADER),
                HomeItem(viewType = HomeItem.VIEW_TYPE_MENU)
            )
            if (homeAppSetting.showHomeContinueWatching != false) {
                initialItems.add(HomeItem(viewType = HomeItem.VIEW_TYPE_CONTINUE_WATCHING))
            }
            if (homeAppSetting.showHomeReleasingToday != false) {
                initialItems.add(HomeItem(viewType = HomeItem.VIEW_TYPE_RELEASING_TODAY))
            }
            if (homeAppSetting.showHomeTrendingAnime != false) {
                initialItems.add(HomeItem(viewType = HomeItem.VIEW_TYPE_TRENDING_ANIME))
            }
            if (homeAppSetting.showHomeTrendingManga != false) {
                initialItems.add(HomeItem(viewType = HomeItem.VIEW_TYPE_TRENDING_MANGA))
            }
            _homeItemList.onNext(initialItems)
        }

        requestHomeData(if (isReloading) Source.NETWORK else null)
    }

    private fun requestHomeData(source: Source?) {
        state = State.LOADING

        disposables.add(
            contentRepository.getHomeData(source)
                .applyScheduler()
                .subscribe(
                    {
                        val currentHomeItems = ArrayList(_homeItemList.value ?: listOf())
                        val trendingAnimeIndex = currentHomeItems.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_TRENDING_ANIME }
                        if (trendingAnimeIndex != -1) {
                            currentHomeItems[trendingAnimeIndex] = HomeItem(media = it.trendingAnime, viewType = HomeItem.VIEW_TYPE_TRENDING_ANIME)
                        }
                        val trendingMangaIndex = currentHomeItems.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_TRENDING_MANGA }
                        if (trendingMangaIndex != -1) {
                            currentHomeItems[trendingMangaIndex] = HomeItem(media = it.trendingManga, viewType = HomeItem.VIEW_TYPE_TRENDING_MANGA)
                        }
                        _homeItemList.onNext(currentHomeItems)
                        _loading.onNext(false)
                        state = State.LOADED
                    },
                    {
                        if (source == Source.CACHE) {
                            _error.onNext(it.getStringResource())
                            _loading.onNext(false)
                            state = State.ERROR
                        } else {
                            requestHomeData(Source.CACHE)
                        }
                    }
                )
        )
    }

    private fun loadContinueWatching(preferNetwork: Boolean = false) {
        if (homeAppSetting.showHomeContinueWatching == false) return

        disposables.add(
            userRepository.getViewer(Source.CACHE)
                .onErrorResumeNext { _: Throwable -> userRepository.getViewer(Source.NETWORK) }
                .flatMap { user ->
                    if (preferNetwork) {
                        mediaListRepository.getMediaListCollection(Source.NETWORK, user, MediaType.ANIME)
                    } else {
                        mediaListRepository.getMediaListCollection(Source.CACHE, user, MediaType.ANIME)
                            .onErrorResumeNext { _: Throwable ->
                                mediaListRepository.getMediaListCollection(Source.NETWORK, user, MediaType.ANIME)
                            }
                    }
                }
                .map { collection ->
                    collection.lists.flatMap { group -> group.entries }
                        .filter { it.status == MediaListStatus.CURRENT || it.status == MediaListStatus.REPEATING }
                        .sortedByDescending { it.updatedAt }
                        .take(6)
                }
                .applyScheduler()
                .subscribe(
                    { mediaLists ->
                        val currentHomeList = ArrayList(_homeItemList.value ?: listOf())
                        val itemIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_CONTINUE_WATCHING }
                        val continueWatchingItem = HomeItem(
                            continueWatching = mediaLists,
                            viewType = HomeItem.VIEW_TYPE_CONTINUE_WATCHING
                        )
                        if (itemIndex != -1) {
                            currentHomeList[itemIndex] = continueWatchingItem
                        } else {
                            val menuIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_MENU }
                            if (menuIndex == -1) return@subscribe
                            val insertIndex = menuIndex + 1
                            currentHomeList.add(insertIndex.coerceAtMost(currentHomeList.size), continueWatchingItem)
                        }
                        _homeItemList.onNext(currentHomeList)
                    },
                    { _error.onNext(it.getStringResource()) }
                )
        )
    }

    private fun updateContinueWatchingEntry(updatedMediaList: MediaList) {
        if (homeAppSetting.showHomeContinueWatching == false) return

        val currentHomeList = ArrayList(_homeItemList.value ?: listOf())
        if (currentHomeList.isEmpty()) return

        val itemIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_CONTINUE_WATCHING }
        val oldEntries = if (itemIndex != -1) currentHomeList[itemIndex].continueWatching else emptyList()
        val updatedMediaId = updatedMediaList.media.getId()
        val matchIndex = oldEntries.indexOfFirst { existing ->
            (updatedMediaList.id != null && existing.id == updatedMediaList.id) ||
                (updatedMediaId != 0 && existing.media.getId() == updatedMediaId)
        }

        // The mutation response can contain only a partial Media object. If the
        // entry is absent or status is missing, reload the full list rather than
        // adding a card with a blank title/poster or removing a valid entry.
        if (updatedMediaList.status == null) {
            loadContinueWatching(preferNetwork = true)
            return
        }
        if (matchIndex == -1) {
            if (updatedMediaList.status == MediaListStatus.CURRENT || updatedMediaList.status == MediaListStatus.REPEATING) {
                loadContinueWatching(preferNetwork = true)
            }
            return
        }

        val nextEntries = oldEntries.toMutableList()
        if (updatedMediaList.status == MediaListStatus.CURRENT || updatedMediaList.status == MediaListStatus.REPEATING) {
            val existing = oldEntries[matchIndex]
            // Keep the complete media metadata from the fetched list; merge only
            // list-entry fields returned by SaveMediaListEntryMutation.
            nextEntries[matchIndex] = existing.copy(
                id = updatedMediaList.id ?: existing.id,
                status = updatedMediaList.status,
                score = updatedMediaList.score,
                progress = updatedMediaList.progress,
                progressVolumes = updatedMediaList.progressVolumes,
                repeat = updatedMediaList.repeat,
                priority = updatedMediaList.priority,
                private = updatedMediaList.private,
                notes = updatedMediaList.notes,
                hiddenFromStatusLists = updatedMediaList.hiddenFromStatusLists,
                customLists = updatedMediaList.customLists,
                advancedScores = updatedMediaList.advancedScores,
                startedAt = updatedMediaList.startedAt,
                completedAt = updatedMediaList.completedAt,
                updatedAt = updatedMediaList.updatedAt,
                createdAt = updatedMediaList.createdAt,
                media = existing.media
            )
        } else {
            nextEntries.removeAt(matchIndex)
        }

        val sortedEntries = nextEntries.sortedByDescending { it.updatedAt }.take(6)
        currentHomeList[itemIndex] = currentHomeList[itemIndex].copy(continueWatching = sortedEntries)
        _homeItemList.onNext(currentHomeList)
    }

    fun loadSearchCategories() {
        val list = ArrayList<ListItem<SearchCategory>>()
        list.add(ListItem(R.string.search_anime, SearchCategory.ANIME))
        list.add(ListItem(R.string.search_manga, SearchCategory.MANGA))
        list.add(ListItem(R.string.search_characters, SearchCategory.CHARACTER))
        list.add(ListItem(R.string.search_staff, SearchCategory.STAFF))
        list.add(ListItem(R.string.search_studios, SearchCategory.STUDIO))
        list.add(ListItem(R.string.search_users, SearchCategory.USER))
        _searchCategoryList.onNext(list)
    }

    fun loadExploreCategories() {
        val list = ArrayList<ListItem<SearchCategory>>()
        list.add(ListItem(R.string.explore_anime, SearchCategory.ANIME))
        list.add(ListItem(R.string.explore_manga, SearchCategory.MANGA))
        list.add(ListItem(R.string.explore_characters, SearchCategory.CHARACTER))
        list.add(ListItem(R.string.explore_staff, SearchCategory.STAFF))
        list.add(ListItem(R.string.explore_studios, SearchCategory.STUDIO))
        _exploreCategoryList.onNext(list)
    }

    fun updateProgress(mediaList: MediaList, newProgress: Int) {
        if (mediaList.progress == newProgress)
            return

        val maxProgress = mediaList.media.episodes

        var targetProgress = newProgress
        var status: MediaListStatus? = null
        var repeat: Int? = null

        if (maxProgress != null && newProgress >= maxProgress) {
            if (mediaList.status == MediaListStatus.REPEATING)
                repeat = mediaList.repeat + 1

            status = MediaListStatus.COMPLETED
            targetProgress = maxProgress
        } else {
            if (mediaList.status == MediaListStatus.PLANNING ||
                mediaList.status == MediaListStatus.PAUSED ||
                mediaList.status == MediaListStatus.DROPPED
            ) {
                status = MediaListStatus.CURRENT
            }
        }
        _loading.onNext(true)

        disposables.add(
            mediaListRepository.updateMediaListProgress(
                MediaType.ANIME,
                mediaList.id ?: 0,
                status,
                repeat,
                targetProgress,
                null
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
}