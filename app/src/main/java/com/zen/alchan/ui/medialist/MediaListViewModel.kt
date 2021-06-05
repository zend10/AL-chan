package com.zen.alchan.ui.medialist

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.MediaType

class MediaListViewModel(
    private val mediaListRepository: MediaListRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _mediaListItems = BehaviorSubject.createDefault(listOf<MediaListItem>())
    val mediaListItems: Observable<List<MediaListItem>>
        get() = _mediaListItems

    private val _listStyleAndAppSetting = PublishSubject.create<Pair<ListStyle, AppSetting>>()
    val listStyleAndAppSetting: Observable<Pair<ListStyle, AppSetting>>
        get() = _listStyleAndAppSetting

    var mediaType: MediaType? = null
    var userId = 0
    var appSetting = AppSetting.EMPTY_APP_SETTING

    override fun loadData() {
        if (state == State.LOADED) return
        state = State.LOADING
        _loading.onNext(true)

        mediaType?.let { mediaType ->
            disposables.add(
                userRepository.getListStyle(mediaType)
                    .zipWith(
                        Observable.zip(
                            userRepository.getAppSetting(),
                            userRepository.getViewer(Source.CACHE)
                        ) { appSetting, user ->
                            return@zip appSetting to user
                        }
                    ) { listStyle, appSettingAndUser ->
                        return@zipWith listStyle to appSettingAndUser
                    }
                    .applyScheduler()
                    .subscribe { (listStyle, appSettingAndUser) ->
                        if (userId == 0) {
                            userId = appSettingAndUser.second.id
                        }

                        appSetting = appSettingAndUser.first
                        _listStyleAndAppSetting.onNext(listStyle to appSetting)

                        getMediaListCollection(state == State.LOADED || state == State.ERROR)
                    }
            )
        }
    }

    fun reloadData() {
        getMediaListCollection(true)
    }

    private fun getMediaListCollection(isReloading: Boolean = false) {
        if (isReloading)
            _loading.onNext(true)

        disposables.add(
            mediaListRepository.getMediaListCollection(userId, mediaType ?: MediaType.ANIME)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { mediaListCollection ->
                        val tempList = ArrayList<MediaListItem>()
                        mediaListCollection.lists.forEach { mediaListGroup ->
                            tempList.add(MediaListItem(title = mediaListGroup.name, viewType = MediaListItem.VIEW_TYPE_TITLE))

                            mediaListGroup.entries.forEach { mediaList ->
                                tempList.add(MediaListItem(mediaList= mediaList, viewType = MediaListItem.VIEW_TYPE_MEDIA_LIST))
                            }
                        }
                        _mediaListItems.onNext(tempList)

                        state = State.LOADED
                    },
                    {
                        state = State.ERROR
                    }
                )
        )
    }
}