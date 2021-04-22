package com.zen.alchan.ui.main

import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.NavigationManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SharedMainViewModel : BaseViewModel() {

    private val _scrollHomeToTop = PublishSubject.create<Unit>()
    private val _scrollAnimeToTop = PublishSubject.create<Unit>()
    private val _scrollMangaToTop = PublishSubject.create<Unit>()
    private val _scrollSocialToTop = PublishSubject.create<Unit>()
    private val _scrollProfileToTop = PublishSubject.create<Unit>()

//    private val scrollEventMap = linkedMapOf(
//        NavigationManager.Page.HOME to _scrollAnimeToTop,
//        NavigationManager.Page.ANIME to _scrollAnimeToTop,
//        NavigationManager.Page.MANGA to _scrollMangaToTop,
//        NavigationManager.Page.SOCIAL to _scrollSocialToTop,
//        NavigationManager.Page.PROFILE to _scrollProfileToTop
//    )

//    fun scrollToTop(pageIndex: Int) {
//        scrollEventMap.toList()[pageIndex].second.onNext(Unit)
//    }

//    fun getScrollToTopObservable(page: NavigationManager.Page): Observable<Unit> {
//        return scrollEventMap[page] ?: _scrollHomeToTop
//    }
}