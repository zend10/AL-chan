package com.zen.alchan.ui.studio

import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Studio
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.StudioItem
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class StudioViewModel(
    private val browseRepository: BrowseRepository,
    private val userRepository: UserRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel<StudioParam>() {

    private val _studioAdapterComponent = PublishSubject.create<AppSetting>()
    val studioAdapterComponent: Observable<AppSetting>
        get() = _studioAdapterComponent

    private val _studioName = BehaviorSubject.createDefault("")
    val studioName: Observable<String>
        get() = _studioName

    private val _mediaCount = BehaviorSubject.createDefault(0)
    val mediaCount: Observable<Int>
        get() = _mediaCount

    private val _mediaCountVisibility = BehaviorSubject.createDefault(false)
    val mediaCountVisibility: Observable<Boolean>
        get() = _mediaCountVisibility

    private val _favoritesCount = BehaviorSubject.createDefault(0)
    val favoritesCount: Observable<Int>
        get() = _favoritesCount

    private val _isFavorite = BehaviorSubject.createDefault(false)
    val isFavorite: Observable<Boolean>
        get() = _isFavorite

    private val _studioItemList = BehaviorSubject.createDefault(listOf<StudioItem>())
    val studioItemList: Observable<List<StudioItem>>
        get() = _studioItemList

    private val _studioLink = PublishSubject.create<String>()
    val studioLink: Observable<String>
        get() = _studioLink

    private var studioId = 0

    private var studio: Studio = Studio()
    private var appSetting: AppSetting = AppSetting()

    override fun loadData(param: StudioParam) {
        loadOnce {
            this.studioId = param.studioId

            disposables.add(
                userRepository.getIsAuthenticated().zipWith(userRepository.getAppSetting()) { isAuthenticated, appSetting ->
                    return@zipWith isAuthenticated to appSetting
                }
                    .applyScheduler()
                    .subscribe { (isAuthenticated, appSetting) ->
                        this.appSetting = appSetting
                        _isAuthenticated.onNext(isAuthenticated)
                        _studioAdapterComponent.onNext(appSetting)
                        loadStudio()
                    }
            )
        }

        if (studio.id != 0)
            checkFavorite()
    }

    fun reloadData() {
        loadStudio()
    }

    private fun checkFavorite() {
        if (studio.id == 0)
            return

        if (_isAuthenticated.value != true) {
            _isAuthenticated.onNext(_isAuthenticated.value ?: false)
            return
        }

        disposables.add(
            userRepository.getViewer(Source.CACHE)
                .map {
                    it.favourites.studios.nodes.find { it.id == studio.id } != null
                }
                .applyScheduler()
                .subscribe {
                    _isFavorite.onNext(it)
                }
        )
    }

    private fun loadStudio() {
        _loading.onNext(true)

        disposables.add(
            browseRepository.getStudio(studioId, 1)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { studio ->
                        this.studio = studio

                        _studioName.onNext(studio.name)
                        _mediaCount.onNext(studio.media.pageInfo.total)
                        _mediaCountVisibility.onNext(!studio.media.pageInfo.hasNextPage)
                        _favoritesCount.onNext(studio.favourites)
                        _isFavorite.onNext(studio.isFavourite)

                        val itemList = ArrayList<StudioItem>()

                        if (studio.media.edges.isNotEmpty())
                            itemList.add(StudioItem(studio = studio, viewType = StudioItem.VIEW_TYPE_MEDIA))

                        _studioItemList.onNext(itemList)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun loadStudioLink() {
        _studioLink.onNext(studio.siteUrl)
    }

    fun copyStudioLink() {
        disposables.add(
            clipboardService.copyPlainText(studio.siteUrl)
                .applyScheduler()
                .subscribe(
                    {
                        _success.onNext(R.string.link_copied)
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }

    fun toggleFavorite() {
        _loading.onNext(true)

        disposables.add(
            userRepository.toggleFavorite(studioId = studio.id)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        val isFavorited = _isFavorite.value ?: false
                        _isFavorite.onNext(!isFavorited)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }
}