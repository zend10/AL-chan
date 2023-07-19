package com.zen.alchan.ui.social

import com.zen.alchan.R
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.SocialAdapterComponent
import com.zen.alchan.helper.pojo.SocialItem
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.zen.alchan.type.LikeableType

class SocialViewModel(
    private val userRepository: UserRepository,
    private val socialRepository: SocialRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel<Unit>() {

    private val _socialItemList = BehaviorSubject.createDefault(listOf<SocialItem>())
    val socialItemList: Observable<List<SocialItem>>
        get() = _socialItemList

    private val _adapterComponent = PublishSubject.create<SocialAdapterComponent>()
    val adapterComponent: Observable<SocialAdapterComponent>
        get() = _adapterComponent

    private var viewer = User()

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                userRepository.getAppSetting()
                    .zipWith(userRepository.getViewer(Source.CACHE)) { appSetting, viewer ->
                        this.viewer = viewer
                        SocialAdapterComponent(viewer = viewer, appSetting = appSetting)
                    }
                    .applyScheduler()
                    .subscribe {
                        _adapterComponent.onNext(it)
                        loadSocialData()
                    }
            )
        }
    }

    fun checkIfNeedReload() {
        disposables.add(
            socialRepository.newOrEditedActivity
                .applyScheduler()
                .filter { it.data != null }
                .subscribe {
                    val activity = it.data
                    val currentActivities = ArrayList(_socialItemList.value ?: listOf())
                    val index = currentActivities.indexOfFirst { it?.activity?.id == activity?.id }
                    if (index != -1) {
                        currentActivities[index] = currentActivities[index]?.copy(activity = activity)
                        _socialItemList.onNext(currentActivities)
                    } else {
                        currentActivities.add(1, SocialItem(activity, viewType = SocialItem.VIEW_TYPE_ACTIVITY))
                        _socialItemList.onNext(currentActivities)
                    }
                }
        )
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
                                    SocialItem(activity = activity, viewType = SocialItem.VIEW_TYPE_ACTIVITY)
                                }
                            )
                            newSocialItemList.add(SocialItem(viewType = SocialItem.VIEW_TYPE_FRIENDS_ACTIVITY_SEE_MORE))
                        }
                        if (it.globalActivities.isNotEmpty()) {
                            newSocialItemList.add(SocialItem(viewType = SocialItem.VIEW_TYPE_GLOBAL_ACTIVITY_HEADER))
                            newSocialItemList.addAll(
                                it.globalActivities.map { activity ->
                                    SocialItem(activity = activity, viewType = SocialItem.VIEW_TYPE_ACTIVITY)
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
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    {
                        val currentSocialItems = _socialItemList.value ?: listOf()
                        val editedActivityIndex = currentSocialItems.indexOfFirst { it.activity?.id == activity.id }
                        if (editedActivityIndex != -1) {
                            val isNowLiked = !activity.isLiked
                            val likeUsers = ArrayList(activity.likes)
                            if (isNowLiked) likeUsers.add(viewer) else likeUsers.removeAll { it.id == viewer.id }
                            currentSocialItems[editedActivityIndex].activity?.isLiked = isNowLiked
                            currentSocialItems[editedActivityIndex].activity?.likeCount = if (isNowLiked) activity.likeCount + 1 else activity.likeCount - 1
                            currentSocialItems[editedActivityIndex].activity?.likes = likeUsers
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
                .applyScheduler()
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

    fun deleteActivity(activity: Activity) {
        _loading.onNext(true)

        disposables.add(
            socialRepository.deleteActivity(activity.id)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    {
                        val currentSocialItems = ArrayList(_socialItemList.value ?: listOf())

                        if (currentSocialItems.size <= 5)
                            reloadData()
                        else {
                            val editedActivityIndex = currentSocialItems.indexOfFirst { it.activity?.id == activity.id }
                            if (editedActivityIndex != -1) {
                                currentSocialItems.removeAt(editedActivityIndex)
                                _socialItemList.onNext(currentSocialItems)
                            }
                        }
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun handleActivityDetailResult(activity: Activity, isDeleted: Boolean) {
        val currentSocialItems = ArrayList(_socialItemList.value ?: listOf())

        if (isDeleted) {
            if (currentSocialItems.size <= 5)
                reloadData()
            else {
                val editedActivityIndex = currentSocialItems.indexOfFirst { it.activity?.id == activity.id }
                if (editedActivityIndex != -1) {
                    currentSocialItems.removeAt(editedActivityIndex)
                    _socialItemList.onNext(currentSocialItems)
                }
            }
        } else {
            val editedActivityIndex = currentSocialItems.indexOfFirst { it.activity?.id == activity.id }
            if (editedActivityIndex != -1) {
                currentSocialItems.removeAt(editedActivityIndex)
                currentSocialItems.add(editedActivityIndex, SocialItem(activity = activity, viewType = SocialItem.VIEW_TYPE_ACTIVITY))
                _socialItemList.onNext(currentSocialItems)
            }
        }
    }

    fun setActivityToBeEdited(activity: Activity) {
        socialRepository.updateActivityToBeEdited(activity)
    }
}