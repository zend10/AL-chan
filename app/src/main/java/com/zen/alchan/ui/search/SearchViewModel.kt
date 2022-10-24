package com.zen.alchan.ui.search

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
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

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentSearchItems = ArrayList(_searchItems.value ?: listOf())
            currentSearchItems.add(null)
            _searchItems.onNext(currentSearchItems)

            doSearch(currentSearchQuery, true)
        }
    }

    fun doSearch(searchQuery: String, isLoadingNextPage: Boolean = false) {
        currentSearchQuery = searchQuery

        when (currentSearchCategory) {
            SearchCategory.ANIME -> searchMedia(MediaType.ANIME, isLoadingNextPage)
            else -> searchMedia(MediaType.MANGA, isLoadingNextPage)
        }
    }

    private fun searchMedia(mediaType: MediaType, isLoadingNextPage: Boolean) {
        state = State.LOADING

        disposables.add(
            contentRepository.searchMedia(currentSearchQuery, mediaType, currentPage)
                .applyScheduler()
                .doFinally {
                    _emptyLayoutVisibility.onNext(_searchItems.value?.isNotEmpty() != false)
                }
                .subscribe(
                    {
                        hasNextPage = it.pageInfo.hasNextPage
                        currentPage = it.pageInfo.currentPage

                        val newSearchItems = it.data.map {
                            SearchItem(media = it, searchCategory = currentSearchCategory)
                        }

                        if (isLoadingNextPage) {
                            val currentSearchItems = ArrayList(_searchItems.value ?: listOf())
                            currentSearchItems.remove(null)
                            currentSearchItems.addAll(newSearchItems)
                            _searchItems.onNext(newSearchItems)
                        } else {
                            _searchItems.onNext(newSearchItems)
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
}