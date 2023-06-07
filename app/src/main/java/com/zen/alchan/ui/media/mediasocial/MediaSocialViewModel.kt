package com.zen.alchan.ui.media.mediasocial

import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.MediaSocialItem
import com.zen.alchan.helper.pojo.SocialAdapterComponent
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class MediaSocialViewModel(
    private val userRepository: UserRepository,
    private val browseRepository: BrowseRepository
) : BaseViewModel<MediaSocialParam>() {

    private val _socialItemList = BehaviorSubject.createDefault(listOf<MediaSocialItem>())
    val socialItemList: Observable<List<MediaSocialItem>>
        get() = _socialItemList

    private val _adapterComponent = PublishSubject.create<SocialAdapterComponent>()
    val adapterComponent: Observable<SocialAdapterComponent>
        get() = _adapterComponent

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private var mediaId = 0

    private var followingMediaListHasNextPage = false
    private var followingMediaListCurrentPage = 0

    private var mediaActivityHasNextPage = false
    private var mediaActivityCurrentPage = 0

    override fun loadData(param: MediaSocialParam) {
        loadOnce {
            mediaId = param.media.getId()

            disposables.add(
                userRepository.getAppSetting()
                    .zipWith(userRepository.getViewer(Source.CACHE)) { appSetting, viewer ->
                        SocialAdapterComponent(viewer, appSetting)
                    }
                    .applyScheduler()
                    .subscribe {
                        _adapterComponent.onNext(it)
                        loadMediaSocial()
                    }
            )
        }
    }

    fun reloadData() {
        loadMediaSocial()
    }

    private fun loadMediaSocial() {
        _loading.onNext(true)
        state = State.LOADING

        disposables.add(
            Observable.zip(
                browseRepository.getMediaFollowingMediaList(mediaId, 1),
                browseRepository.getMediaActivity(mediaId, 1)
            ) { followingMediaList, mediaActivity ->
                followingMediaList to mediaActivity
            }
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                    _emptyLayoutVisibility.onNext(_socialItemList.value.isNullOrEmpty())
                }
                .subscribe(
                    { (followingMediaList, mediaActivity) ->
                        followingMediaListHasNextPage = followingMediaList.pageInfo.hasNextPage
                        followingMediaListCurrentPage = followingMediaList.pageInfo.currentPage

                        mediaActivityHasNextPage = mediaActivity.pageInfo.hasNextPage
                        mediaActivityCurrentPage = mediaActivity.pageInfo.currentPage

                        val newSocialItems = ArrayList<MediaSocialItem>()

                        if (followingMediaList.data.isNotEmpty()) {
                            newSocialItems.add(MediaSocialItem(viewType = MediaSocialItem.VIEW_TYPE_FOLLOWING_MEDIA_LIST_HEADER))
                            newSocialItems.addAll(
                                followingMediaList.data.map {
                                    MediaSocialItem(mediaList = it, viewType = MediaSocialItem.VIEW_TYPE_FOLLOWING_MEDIA_LIST)
                                }
                            )
                            if (followingMediaListHasNextPage)
                                newSocialItems.add(MediaSocialItem(viewType = MediaSocialItem.VIEW_TYPE_FOLLOWING_MEDIA_LIST_SEE_MORE))
                        }

                        if (mediaActivity.data.isNotEmpty()) {
                            newSocialItems.add(MediaSocialItem(viewType = MediaSocialItem.VIEW_TYPE_MEDIA_ACTIVITY_HEADER))
                            newSocialItems.addAll(
                                mediaActivity.data.map {
                                    MediaSocialItem(activity = it, viewType = MediaSocialItem.VIEW_TYPE_MEDIA_ACTIVITY)
                                }
                            )
                            if (mediaActivityHasNextPage)
                                newSocialItems.add(MediaSocialItem(viewType = MediaSocialItem.VIEW_TYPE_MEDIA_ACTIVITY_SEE_MORE))
                        }

                        _socialItemList.onNext(newSocialItems)
                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    fun loadFollowingMediaListNextPage() {
        _loading.onNext(true)
        state = State.LOADING

        disposables.add(
            browseRepository.getMediaFollowingMediaList(mediaId, followingMediaListCurrentPage + 1)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        followingMediaListHasNextPage = it.pageInfo.hasNextPage
                        followingMediaListCurrentPage = it.pageInfo.currentPage

                        val currentSocialItems = ArrayList(_socialItemList.value ?: listOf())
                        val seeMoreIndex = currentSocialItems.indexOfFirst { it.viewType == MediaSocialItem.VIEW_TYPE_FOLLOWING_MEDIA_LIST_SEE_MORE }

                        if (!followingMediaListHasNextPage)
                            currentSocialItems.removeAt(seeMoreIndex)

                        currentSocialItems.addAll(
                            seeMoreIndex,
                            it.data.map { MediaSocialItem(mediaList = it, viewType = MediaSocialItem.VIEW_TYPE_FOLLOWING_MEDIA_LIST) }
                        )

                        _socialItemList.onNext(currentSocialItems)

                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    fun loadMediaActivityNextPage() {
        _loading.onNext(true)
        state = State.LOADING

        disposables.add(
            browseRepository.getMediaActivity(mediaId, mediaActivityCurrentPage + 1)
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        mediaActivityHasNextPage = it.pageInfo.hasNextPage
                        mediaActivityCurrentPage = it.pageInfo.currentPage

                        val currentSocialItems = ArrayList(_socialItemList.value ?: listOf())
                        val seeMoreIndex = currentSocialItems.indexOfFirst { it.viewType == MediaSocialItem.VIEW_TYPE_MEDIA_ACTIVITY_SEE_MORE }

                        if (!mediaActivityHasNextPage)
                            currentSocialItems.removeAt(seeMoreIndex)

                        currentSocialItems.addAll(
                            seeMoreIndex,
                            it.data.map { MediaSocialItem(activity = it, viewType = MediaSocialItem.VIEW_TYPE_MEDIA_ACTIVITY) }
                        )

                        _socialItemList.onNext(currentSocialItems)

                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }
}