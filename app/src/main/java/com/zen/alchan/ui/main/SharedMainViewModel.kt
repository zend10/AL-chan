package com.zen.alchan.ui.main

import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SharedMainViewModel : BaseViewModel() {

    private val scrollHomeToTopSubject = PublishSubject.create<Unit>()
    private val scrollAnimeToTopSubject = PublishSubject.create<Unit>()
    private val scrollMangaToTopSubject = PublishSubject.create<Unit>()
    private val scrollSocialToTopSubject = PublishSubject.create<Unit>()
    private val scrollProfileToTopSubject = PublishSubject.create<Unit>()

    private val scrollEvents = linkedMapOf(
        Page.HOME to scrollAnimeToTopSubject,
        Page.SOCIAL to scrollSocialToTopSubject,
        Page.ANIME to scrollAnimeToTopSubject,
        Page.MANGA to scrollMangaToTopSubject,
        Page.PROFILE to scrollProfileToTopSubject
    )

    fun scrollToTop(pageIndex: Int) {
        scrollEvents.toList()[pageIndex].second.onNext(Unit)
    }

    fun getScrollToTopObservable(page: Page): Observable<Unit> {
        return scrollEvents[page] ?: scrollHomeToTopSubject
    }

    enum class Page {
        HOME,
        SOCIAL,
        ANIME,
        MANGA,
        PROFILE
    }
}