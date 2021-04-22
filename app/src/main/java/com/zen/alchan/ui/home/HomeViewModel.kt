package com.zen.alchan.ui.home

import com.zen.alchan.data.response.Media
import com.zen.alchan.data.response.Review
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.response.HomeData
import com.zen.alchan.helper.extensions.sendMessage
import com.zen.alchan.helper.pojo.HomeItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class HomeViewModel(private val contentRepository: ContentRepository) : BaseViewModel() {

//    private val errorSubject = PublishSubject.create<Int>()
//    private val loadingSubject = BehaviorSubject.createDefault(false)
    private val homeItemListSubject = BehaviorSubject.createDefault(listOf<HomeItem>())

//    val error: Observable<Int>
//        get() = errorSubject
//
//    val loading: Observable<Boolean>
//        get() = loadingSubject

    val homeItemList: Observable<List<HomeItem>>
        get() = homeItemListSubject

    private var isLoaded = false

    fun getHomeData(isReloading: Boolean = false) {
        if (!isReloading && isLoaded) return

        if (isReloading)
            loadingSubject.onNext(true)
        else
            homeItemListSubject.onNext(
                listOf(HomeItem.ITEM_HEADER, HomeItem.ITEM_MENU, HomeItem.EMPTY_TRENDING_ANIME, HomeItem.EMPTY_TRENDING_MANGA)
            )

        disposables.add(
            contentRepository.getHomeData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        homeItemListSubject.onNext(
                            listOf(
                                HomeItem.ITEM_HEADER,
                                HomeItem.ITEM_MENU,
                                HomeItem(media = it.trendingAnime, viewType = HomeItem.VIEW_TYPE_TRENDING_ANIME),
                                HomeItem(media = it.trendingManga, viewType = HomeItem.VIEW_TYPE_TRENDING_MANGA)
                            )
                        )
                        loadingSubject.onNext(false)
                        isLoaded = true
                    },
                    {
                        errorSubject.onNext(it.sendMessage())
                        loadingSubject.onNext(false)
                        isLoaded = true
                    }
                )
        )
    }
}