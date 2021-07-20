package com.zen.alchan.ui.medialist

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.pojo.MediaListAdapterComponent
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import type.MediaType

class MediaListViewModel(
    private val mediaListRepository: MediaListRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _mediaListAdapterComponent = BehaviorSubject.createDefault(MediaListAdapterComponent.EMPTY_MEDIA_LIST_ADAPTER_COMPONENT)
    val mediaListAdapterComponent: Observable<MediaListAdapterComponent>
        get() = _mediaListAdapterComponent

    var mediaType: MediaType? = null
    var userId = 0
    var user = User.EMPTY_USER
    var listStyle = ListStyle.EMPTY_LIST_STYLE
    var appSetting = AppSetting.EMPTY_APP_SETTING

    override fun loadData() {
        if (state == State.LOADED) return
        state = State.LOADING
        _loading.onNext(true)

        mediaType?.let { mediaType ->
            disposables.add(
                userRepository.getIsAuthenticated()
                    .applyScheduler()
                    .filter { it }
                    .flatMap {
                        Observable.zip(
                            userRepository.getListStyle(com.zen.alchan.helper.enums.MediaType.valueOf(mediaType.name)),
                            userRepository.getAppSetting(),
                            userRepository.getViewer(Source.CACHE)
                        ) { listStyle, appSetting, user ->
                            return@zip Triple(listStyle, appSetting, user)
                        }
                    }
                    .subscribe { (listStyle, appSetting, user) ->
                        if (userId == 0) {
                            userId = user.id
                        }

                        this.user = user
                        this.listStyle = listStyle
                        this.appSetting = appSetting

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
                        _mediaListAdapterComponent.onNext(MediaListAdapterComponent(listStyle, appSetting, user.mediaListOptions, tempList))

                        state = State.LOADED
                    },
                    {
                        state = State.ERROR
                    }
                )
        )
    }
}