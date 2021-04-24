package com.zen.alchan.ui.main

import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SharedMainViewModel : BaseViewModel() {

    private val _scrollHomeToTop = PublishSubject.create<Unit>()
    private val _scrollAnimeToTop = PublishSubject.create<Unit>()
    private val _scrollMangaToTop = PublishSubject.create<Unit>()
    private val _scrollSocialToTop = PublishSubject.create<Unit>()
    private val _scrollProfileToTop = PublishSubject.create<Unit>()

    private val scrollEventMap = linkedMapOf(
        Page.HOME to _scrollAnimeToTop,
        Page.SOCIAL to _scrollSocialToTop,
        Page.ANIME to _scrollAnimeToTop,
        Page.MANGA to _scrollMangaToTop,
        Page.PROFILE to _scrollProfileToTop
    )

    fun scrollToTop(pageIndex: Int) {
        scrollEventMap.toList()[pageIndex].second.onNext(Unit)
    }

    fun getScrollToTopObservable(page: Page): Observable<Unit> {
        return scrollEventMap[page] ?: _scrollHomeToTop
    }

    enum class Page {
        HOME,
        SOCIAL,
        ANIME,
        MANGA,
        PROFILE
    }
}