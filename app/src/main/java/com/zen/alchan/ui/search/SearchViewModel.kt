package com.zen.alchan.ui.search

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.SearchItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaType

class SearchViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : BaseViewModel<Unit>() {

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

    private val _searchPlaceholderText = BehaviorSubject.createDefault(R.string.search_anime)
    val searchPlaceholderText: Observable<Int>
        get() = _searchPlaceholderText

    private val _scrollToTopTrigger = PublishSubject.create<Unit>()
    val scrollToTopTrigger: Observable<Unit>
        get() = _scrollToTopTrigger

    private var currentSearchCategory = SearchCategory.ANIME
    private var currentSearchQuery = ""

    private var hasNextPage = false
    private var currentPage = 0

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        _appSetting.onNext(it)
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
        if (searchQuery.isNotBlank() && !isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        currentSearchQuery = searchQuery

        val page = if (isLoadingNextPage) currentPage + 1 else 1

        disposables.add(
            when (currentSearchCategory) {
                SearchCategory.ANIME -> contentRepository.searchMedia(searchQuery, MediaType.ANIME, null, page)
                SearchCategory.MANGA -> contentRepository.searchMedia(searchQuery, MediaType.MANGA, null, page)
                SearchCategory.CHARACTER -> contentRepository.searchCharacter(searchQuery, page)
                SearchCategory.STAFF -> contentRepository.searchStaff(searchQuery, page)
                SearchCategory.STUDIO -> contentRepository.searchStudio(searchQuery, page)
                SearchCategory.USER -> contentRepository.searchUser(searchQuery, page)
            }
                .applyScheduler()
                .doFinally {
                    if (searchQuery.isNotBlank() && !isLoadingNextPage) {
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

    fun updateSelectedSearchCategory(newSearchCategory: SearchCategory) {
        currentSearchCategory = newSearchCategory
        _searchPlaceholderText.onNext(
            when (newSearchCategory) {
                SearchCategory.ANIME -> R.string.search_anime
                SearchCategory.MANGA -> R.string.search_manga
                SearchCategory.CHARACTER -> R.string.search_characters
                SearchCategory.STAFF -> R.string.search_staff
                SearchCategory.STUDIO -> R.string.search_studios
                SearchCategory.USER -> R.string.search_users
            }
        )
        reloadData()
    }

    fun loadSearchCategories() {
        val list = ArrayList<ListItem<SearchCategory>>()
        list.add(ListItem(R.string.anime, SearchCategory.ANIME))
        list.add(ListItem(R.string.manga, SearchCategory.MANGA))
        list.add(ListItem(R.string.characters, SearchCategory.CHARACTER))
        list.add(ListItem(R.string.staff, SearchCategory.STAFF))
        list.add(ListItem(R.string.studios, SearchCategory.STUDIO))
        list.add(ListItem(R.string.users, SearchCategory.USER))
        _searchCategoryList.onNext(list)
    }
}