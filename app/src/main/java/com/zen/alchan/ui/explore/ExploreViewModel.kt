package com.zen.alchan.ui.explore

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.helper.enums.Sort
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.MediaFilterComponent
import com.zen.alchan.helper.pojo.SearchItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.zen.alchan.type.MediaType
import com.zen.alchan.type.ScoreFormat

class ExploreViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : BaseViewModel<ExploreParam>() {

    private val _appSetting = PublishSubject.create<AppSetting>()
    val appSetting: Observable<AppSetting>
        get() = _appSetting

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private val _searchItems = BehaviorSubject.createDefault<List<SearchItem?>>(listOf())
    val searchItems: Observable<List<SearchItem?>>
        get() = _searchItems

    private val _searchCategoryList = PublishSubject.create<List<ListItem<SearchCategory>>>()
    val searchCategoryList: Observable<List<ListItem<SearchCategory>>>
        get() = _searchCategoryList

    private val _searchPlaceholderText = BehaviorSubject.createDefault(R.string.explore_anime)
    val searchPlaceholderText: Observable<Int>
        get() = _searchPlaceholderText

    private val _filterVisibility = BehaviorSubject.createDefault(false)
    val filterVisibility: Observable<Boolean>
        get() = _filterVisibility

    private val _mediaFilterComponent = PublishSubject.create<MediaFilterComponent>()
    val mediaFilterComponent: Observable<MediaFilterComponent>
        get() = _mediaFilterComponent

    private val _scrollToTopTrigger = PublishSubject.create<Unit>()
    val scrollToTopTrigger: Observable<Unit>
        get() = _scrollToTopTrigger

    private var currentSearchCategory = SearchCategory.ANIME
    private var currentSearchQuery = ""
    private var mediaFilter: MediaFilter = MediaFilter(sort = Sort.POPULARITY, orderByDescending = true)

    private var hasNextPage = false
    private var currentPage = 0

    override fun loadData(param: ExploreParam) {
        currentSearchCategory = param.searchCategory
        param.mediaFilter?.let {
            this.mediaFilter = it.copy(sort = Sort.POPULARITY, orderByDescending = true)
        }

        loadOnce {
            updateSelectedSearchCategory(currentSearchCategory, false)

            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        _appSetting.onNext(it)
                        doSearch(currentSearchQuery)
                    }
            )
        }
    }

    fun reloadData() {
        doSearch(currentSearchQuery)
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentSearchItems = ArrayList(_searchItems.value ?: listOf())
            currentSearchItems.add(null)
            _searchItems.onNext(currentSearchItems)

            doSearch(currentSearchQuery, true)
        }
    }

    fun doSearch(searchQuery: String, isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        currentSearchQuery = searchQuery

        val page = if (isLoadingNextPage) currentPage + 1 else 1


        disposables.add(
            when (currentSearchCategory) {
                SearchCategory.ANIME -> contentRepository.searchMedia(searchQuery, MediaType.ANIME, mediaFilter, page)
                SearchCategory.MANGA -> contentRepository.searchMedia(searchQuery, MediaType.MANGA, mediaFilter, page)
                SearchCategory.CHARACTER -> contentRepository.searchCharacter(searchQuery, page)
                SearchCategory.STAFF -> contentRepository.searchStaff(searchQuery, page)
                SearchCategory.STUDIO -> contentRepository.searchStudio(searchQuery, page)
                SearchCategory.USER -> contentRepository.searchUser(searchQuery, page)
            }
                .applyScheduler()
                .doFinally {
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                        _emptyLayoutVisibility.onNext(_searchItems.value?.isEmpty() == true)
                    }
                }
                .subscribe(
                    {
                        hasNextPage = it.pageInfo.hasNextPage
                        currentPage = it.pageInfo.currentPage

                        val newSearchItems = it.data.map {
                            SearchItem(
                                media = it as? Media ?: Media(),
                                character = it as? Character ?: Character(),
                                staff = it as? Staff ?: Staff(),
                                studio = it as? Studio ?: Studio(),
                                user = it as? User ?: User(),
                                searchCategory = currentSearchCategory
                            )
                        }

                        if (isLoadingNextPage) {
                            val currentSearchItems = ArrayList(_searchItems.value ?: listOf())
                            currentSearchItems.remove(null)
                            currentSearchItems.addAll(newSearchItems)
                            _searchItems.onNext(currentSearchItems)
                        } else {
                            _searchItems.onNext(newSearchItems)
                            _scrollToTopTrigger.onNext(Unit)
                        }

                        state = State.LOADED
                    },
                    {
                        if (isLoadingNextPage) {
                            val currentSearchItems = ArrayList(_searchItems.value ?: listOf())
                            currentSearchItems.remove(null)
                            _searchItems.onNext(currentSearchItems)
                        }

                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    fun updateSelectedSearchCategory(newSearchCategory: SearchCategory, shouldReload: Boolean) {
        currentSearchCategory = newSearchCategory
        _searchPlaceholderText.onNext(
            when (newSearchCategory) {
                SearchCategory.ANIME -> R.string.explore_anime
                SearchCategory.MANGA -> R.string.explore_manga
                SearchCategory.CHARACTER -> R.string.explore_characters
                SearchCategory.STAFF -> R.string.explore_staff
                SearchCategory.STUDIO -> R.string.explore_studios
                SearchCategory.USER -> R.string.search_users // should not be used
            }
        )
        _filterVisibility.onNext(
            when (newSearchCategory) {
                SearchCategory.ANIME -> true
                SearchCategory.MANGA -> true
                SearchCategory.CHARACTER -> false
                SearchCategory.STAFF -> false
                SearchCategory.STUDIO -> false
                SearchCategory.USER -> false // should not be used
            }
        )

        if (shouldReload) {
            mediaFilter = mediaFilter.copy(
                mediaFormats = listOf(),
                mediaSeasons = listOf(),
                minEpisodes = null,
                maxEpisodes = null,
                minDuration = null,
                maxDuration = null,
                streamingOn = listOf()
            )
            reloadData()
        }
    }

    fun loadSearchCategories() {
        val list = ArrayList<ListItem<SearchCategory>>()
        list.add(ListItem(R.string.explore_anime, SearchCategory.ANIME))
        list.add(ListItem(R.string.explore_manga, SearchCategory.MANGA))
        list.add(ListItem(R.string.explore_characters, SearchCategory.CHARACTER))
        list.add(ListItem(R.string.explore_staff, SearchCategory.STAFF))
        list.add(ListItem(R.string.explore_studios, SearchCategory.STUDIO))
        _searchCategoryList.onNext(list)
    }

    fun loadMediaFilterComponent() {
        _mediaFilterComponent.onNext(
            MediaFilterComponent(
                mediaFilter,
                when (currentSearchCategory) {
                    SearchCategory.ANIME -> com.zen.alchan.helper.enums.MediaType.ANIME
                    SearchCategory.MANGA -> com.zen.alchan.helper.enums.MediaType.MANGA
                    else -> com.zen.alchan.helper.enums.MediaType.ANIME
                },
                ScoreFormat.POINT_100,
                false,
                false
            )
        )
    }

    fun updateMediaFilter(newFilter: MediaFilter) {
        mediaFilter = newFilter
        reloadData()
    }
}