package com.zen.alchan.ui.home

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.HomeItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class HomeViewModel(
    private val contentRepository: ContentRepository,
    private val userRepository: UserRepository
) : BaseViewModel<Unit>() {

    private val _homeItemList = BehaviorSubject.createDefault(listOf<HomeItem>())
    val homeItemList: Observable<List<HomeItem>>
        get() = _homeItemList

    private val _adapterComponent = PublishSubject.create<AppSetting>()
    val adapterComponent: Observable<AppSetting>
        get() = _adapterComponent

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        _adapterComponent.onNext(it)
                        getHomeData()
                    }
            )
        }
    }

    fun reloadData() {
        getHomeData(true)
    }

    private fun getHomeData(isReloading: Boolean = false) {
        if (!isReloading && state == State.LOADED) return

        if (isReloading)
            _loading.onNext(true)
        else
            _homeItemList.onNext(
                listOf(
                    HomeItem(viewType = HomeItem.VIEW_TYPE_HEADER),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_MENU),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_TRENDING_ANIME),
                    HomeItem(viewType = HomeItem.VIEW_TYPE_TRENDING_MANGA)
                )
            )

        requestHomeData(if (isReloading) Source.NETWORK else null)
    }

    private fun requestHomeData(source: Source?) {
        state = State.LOADING

        disposables.add(
            contentRepository.getHomeData(source)
                .applyScheduler()
                .subscribe(
                    {
                        _homeItemList.onNext(
                            listOf(
                                HomeItem(media = it.trendingAnime.filter { it.bannerImage.isNotBlank() } + it.trendingManga.filter { it.bannerImage.isNotBlank() }, viewType = HomeItem.VIEW_TYPE_HEADER),
                                HomeItem(viewType = HomeItem.VIEW_TYPE_MENU),
                                HomeItem(media = it.trendingAnime, viewType = HomeItem.VIEW_TYPE_TRENDING_ANIME),
                                HomeItem(media = it.trendingManga, viewType = HomeItem.VIEW_TYPE_TRENDING_MANGA)
                            )
                        )
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
}