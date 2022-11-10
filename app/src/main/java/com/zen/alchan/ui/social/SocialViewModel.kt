package com.zen.alchan.ui.social

import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.SocialItem
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.LikeableType

class SocialViewModel(
    private val userRepository: UserRepository,
    private val socialRepository: SocialRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel<Unit>() {

    private val _socialItemList = BehaviorSubject.createDefault(listOf<SocialItem>())
    val socialItemList: Observable<List<SocialItem>>
        get() = _socialItemList

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
                        loadSocialData()
                    }
            )
        }
    }

    fun reloadData() {
        loadSocialData(true)
    }

    private fun loadSocialData(isReloading: Boolean = false) {
        _loading.onNext(true)
        state = State.LOADING

        disposables.add(
            socialRepository.getSocialData()
                .applyScheduler()
                .doFinally {
                    _loading.onNext(false)
                }
                .subscribe(
                    {
                        val newSocialItemList = ArrayList<SocialItem>()
                        if (it.friendsActivities.isNotEmpty()) {
                            newSocialItemList.add(SocialItem(viewType = SocialItem.VIEW_TYPE_FRIENDS_ACTIVITY_HEADER))
                            newSocialItemList.addAll(
                                it.friendsActivities.map { activity ->
                                    SocialItem(activity = activity, viewType = SocialItem.VIEW_TYPE_FRIENDS_ACTIVITY)
                                }
                            )
                            newSocialItemList.add(SocialItem(viewType = SocialItem.VIEW_TYPE_FRIENDS_ACTIVITY_SEE_MORE))
                        }
                        if (it.globalActivities.isNotEmpty()) {
                            newSocialItemList.add(SocialItem(viewType = SocialItem.VIEW_TYPE_GLOBAL_ACTIVITY_HEADER))
                            newSocialItemList.addAll(
                                it.globalActivities.map { activity ->
                                    SocialItem(activity = activity, viewType = SocialItem.VIEW_TYPE_GLOBAL_ACTIVITY)
                                }
                            )
                            newSocialItemList.add(SocialItem(viewType = SocialItem.VIEW_TYPE_GLOBAL_ACTIVITY_SEE_MORE))
                        }
                        _socialItemList.onNext(newSocialItemList)
                        state = State.LOADED
                    },
                    {
                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    fun copyActivityLink(activity: Activity) {
        disposables.add(
            clipboardService.copyPlainText(activity.siteUrl)
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

    fun toggleLike(activity: Activity) {
        _loading.onNext(true)

        disposables.add(
            socialRepository.toggleLike(activity.id, LikeableType.ACTIVITY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    {
                        val currentSocialItems = _socialItemList.value ?: listOf()
                        val editedActivityIndex = currentSocialItems.indexOfFirst { it.activity?.id == activity.id }
                        if (editedActivityIndex != -1) {
                            val isNowLiked = !activity.isLiked
                            currentSocialItems[editedActivityIndex].activity?.isLiked = isNowLiked
                            currentSocialItems[editedActivityIndex].activity?.likeCount = if (isNowLiked) activity.likeCount + 1 else activity.likeCount - 1
                            _socialItemList.onNext(currentSocialItems)
                        }
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun toggleSubscription(activity: Activity) {
        _loading.onNext(true)

        disposables.add(
            socialRepository.toggleActivitySubscription(activity.id, !activity.isSubscribed)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    {
                        val currentSocialItems = _socialItemList.value ?: listOf()
                        val editedActivityIndex = currentSocialItems.indexOfFirst { it.activity?.id == activity.id }
                        if (editedActivityIndex != -1) {
                            currentSocialItems[editedActivityIndex].activity?.isSubscribed = !activity.isSubscribed
                            _socialItemList.onNext(currentSocialItems)
                        }
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }
}