package com.zen.alchan.ui.main

import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SharedMainViewModel : BaseViewModel() {

    private val _scrollHomeToTop = PublishSubject.create<Unit>()
    private val _scrollAnimeToTop = PublishSubject.create<Unit>()
    private val _scrollMangaToTop = PublishSubject.create<Unit>()
    private val _scrollSocialToTop = PublishSubject.create<Unit>()
    private val _scrollProfileToTop = PublishSubject.create<Unit>()

    private val scrollEvents = linkedMapOf(
        Page.HOME to _scrollAnimeToTop,
        Page.ANIME to _scrollAnimeToTop,
        Page.MANGA to _scrollMangaToTop,
        Page.SOCIAL to _scrollSocialToTop,
        Page.PROFILE to _scrollProfileToTop
    )

    private val _bottomSheetNavigation = PublishSubject.create<Int>()
    val bottomSheetNavigation: Observable<Int>
        get() = _bottomSheetNavigation

    override fun loadData() {
        // do nothing
    }

    fun scrollToTop(pageIndex: Int) {
        scrollEvents.toList()[pageIndex].second.onNext(Unit)
    }

    fun getScrollToTopObservable(page: Page): Observable<Unit> {
        return scrollEvents[page] ?: _scrollHomeToTop
    }

    fun getPageFromMediaType(mediaType: MediaType): Page {
        return when (mediaType) {
            MediaType.ANIME -> Page.ANIME
            MediaType.MANGA -> Page.MANGA
        }
    }

    fun navigateTo(page: Page) {
        _bottomSheetNavigation.onNext(Page.values().indexOfFirst { it == page })
    }

    enum class Page {
        HOME,
        ANIME,
        MANGA,
        SOCIAL,
        PROFILE
    }
}