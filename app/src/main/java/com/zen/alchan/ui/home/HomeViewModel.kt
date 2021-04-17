package com.zen.alchan.ui.home

import com.zen.alchan.data.model.Media
import com.zen.alchan.data.model.Review
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class HomeViewModel(private val contentRepository: ContentRepository) : BaseViewModel() {

    private val _loading = BehaviorSubject.createDefault(false)
    private val _error = PublishSubject.create<Int>()
    private val _trendingAnime = BehaviorSubject.createDefault(listOf<Media>())
    private val _trendingManga = BehaviorSubject.createDefault(listOf<Media>())
    private val _newAnime = BehaviorSubject.createDefault(listOf<Media>())
    private val _newManga = BehaviorSubject.createDefault(listOf<Media>())
    private val _review = BehaviorSubject.createDefault(listOf<Review>())


    val loading: Observable<Boolean>
        get() = _loading

    val error: Observable<Int>
        get() = _error

    val trendingAnime: Observable<List<Media>>
        get() = _trendingAnime

    val trendingManga: Observable<List<Media>>
        get() = _trendingManga

    val newAnime: Observable<List<Media>>
        get() = _newAnime

    val newManga: Observable<List<Media>>
        get() = _newManga

    val review: Observable<List<Review>>
        get() = _review


    fun getHomeData() {
        _loading.onNext(true)

        disposables.add(
            contentRepository.getHomeData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    {
                        _trendingAnime.onNext(it.trendingAnime)
                        _trendingManga.onNext(it.trendingManga)
                        _newAnime.onNext(it.newAnime)
                        _newManga.onNext(it.newManga)
                        _review.onNext(it.review)
                    },
                    {
                        _error.onNext(it.sendMessage())
                    }
                )
        )
    }
}