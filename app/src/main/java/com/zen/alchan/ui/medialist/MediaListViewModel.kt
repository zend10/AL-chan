package com.zen.alchan.ui.medialist

import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.enums.getAniListMediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.pojo.MediaListAdapterComponent
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class MediaListViewModel(
    private val mediaListRepository: MediaListRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _toolbarTitle = BehaviorSubject.createDefault(R.string.anime_list)
    val toolbarTitle: Observable<Int>
        get() = _toolbarTitle

    private val _mediaListAdapterComponent = BehaviorSubject.createDefault(MediaListAdapterComponent.EMPTY_MEDIA_LIST_ADAPTER_COMPONENT)
    val mediaListAdapterComponent: Observable<MediaListAdapterComponent>
        get() = _mediaListAdapterComponent

    private val _listStyle = BehaviorSubject.createDefault(ListStyle.EMPTY_LIST_STYLE)
    val listStyle: Observable<ListStyle>
        get() = _listStyle

    var mediaType: MediaType? = null
    var userId = 0

    private var user = User.EMPTY_USER
    private var appSetting = AppSetting.EMPTY_APP_SETTING

    override fun loadData() {
        loadOnce {
            _loading.onNext(true)

            mediaType?.let { mediaType ->
                _toolbarTitle.onNext(
                    when (mediaType) {
                        MediaType.ANIME -> R.string.anime_list
                        MediaType.MANGA -> R.string.manga_list
                    }
                )

                disposables.add(
                    userRepository.getIsAuthenticated()
                        .applyScheduler()
                        .filter { it }
                        .flatMap {
                            Observable.zip(
                                userRepository.getListStyle(MediaType.valueOf(mediaType.name)),
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
                            this.appSetting = appSetting
                            _listStyle.onNext(listStyle)

                            getMediaListCollection(state == State.LOADED || state == State.ERROR)
                        }
                )
            }
        }
    }

    fun reloadData() {
        getMediaListCollection(true)
    }

    private fun getMediaListCollection(isReloading: Boolean = false) {
        if (isReloading)
            _loading.onNext(true)

        disposables.add(
            mediaListRepository.getMediaListCollection(userId, mediaType.getAniListMediaType())
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
                        _mediaListAdapterComponent.onNext(MediaListAdapterComponent(
                            _listStyle.value ?: ListStyle.EMPTY_LIST_STYLE, appSetting, user.mediaListOptions, tempList)
                        )

                        state = State.LOADED
                    },
                    {
                        state = State.ERROR
                    }
                )
        )
    }
}