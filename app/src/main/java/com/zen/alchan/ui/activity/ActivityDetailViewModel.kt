package com.zen.alchan.ui.activity

import com.zen.R
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.ActivityReply
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.SocialAdapterComponent
import com.zen.alchan.helper.pojo.SocialItem
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.type.LikeableType
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

data class ActivityDetailViewModel(
    private val userRepository: UserRepository,
    private val socialRepository: SocialRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel<ActivityDetailParam>() {

    private val _adapterComponent = PublishSubject.create<SocialAdapterComponent>()
    val adapterComponent: Observable<SocialAdapterComponent>
        get() = _adapterComponent

    private val _socialItemList = BehaviorSubject.createDefault(listOf<SocialItem>())
    val socialItemList: Observable<List<SocialItem>>
        get() = _socialItemList

    private val _activityDetailResult = PublishSubject.create<Pair<Activity, Boolean>>()
    val activityDetailResult: Observable<Pair<Activity, Boolean>>
        get() = _activityDetailResult

    private var activityId = 0
    private var viewer = User()

    override fun loadData(param: ActivityDetailParam) {
        loadOnce {
            activityId = param.activityId

            disposables.add(
                userRepository.getAppSetting()
                    .zipWith(userRepository.getViewer(Source.CACHE)) { appSetting, viewer ->
                        this.viewer = viewer
                        SocialAdapterComponent(viewer = viewer, appSetting = appSetting)
                    }
                    .applyScheduler()
                    .subscribe {
                        _adapterComponent.onNext(it)
                        loadActivity()
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
                    currentActivities[0] = currentActivities[0]?.copy(activity = activity)
                    _socialItemList.onNext(currentActivities)
                    socialRepository.clearNewOrEditedActivity()
                }
        )

        disposables.add(
            socialRepository.newOrEditedReply
                .applyScheduler()
                .filter { it.data != null }
                .subscribe {
                    val activityReply = it.data
                    val currentSocialItems = ArrayList(_socialItemList.value ?: listOf())
                    val index = currentSocialItems.indexOfFirst { it?.activityReply?.id == activityReply?.id }
                    if (index != -1) {
                        currentSocialItems[index] = currentSocialItems[index]?.copy(activityReply = activityReply)
                        _socialItemList.onNext(currentSocialItems)
                    } else {
                        currentSocialItems.firstOrNull()?.activity?.let {
                            val replies = ArrayList(it.replies)
                            replies.add(activityReply)
                            it.replies = replies
                            it.replyCount = replies.size
                        }
                        currentSocialItems.add(SocialItem(activity = currentSocialItems.firstOrNull()?.activity, activityReply = activityReply, viewType = SocialItem.VIEW_TYPE_ACTIVITY_REPLY))
                        _socialItemList.onNext(currentSocialItems)
                    }
                    socialRepository.clearNewOrEditedReply()

                    currentSocialItems.firstOrNull()?.activity?.let { activity ->
                        disposables.add(
                            Observable.timer(1000L, TimeUnit.MILLISECONDS)
                                .applyScheduler()
                                .subscribe {
                                    _activityDetailResult.onNext(activity to false)
                                }
                        )
                    }
                }
        )
    }

    fun reloadData() {
        loadActivity()
    }

    private fun loadActivity() {
        _loading.onNext(true)
        state = State.LOADING

        disposables.add(
            socialRepository.getActivityDetail(activityId)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    { activity ->
                        val newSocialItems = ArrayList<SocialItem>()
                        newSocialItems.add(SocialItem(activity = activity, viewType = SocialItem.VIEW_TYPE_ACTIVITY))
                        newSocialItems.addAll(
                            activity.replies.map {
                                SocialItem(activity = activity, activityReply = it, viewType = SocialItem.VIEW_TYPE_ACTIVITY_REPLY)
                            }
                        )
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
        handleLike(activity.id, LikeableType.ACTIVITY)
    }

    fun toggleLike(activityReply: ActivityReply) {
        handleLike(activityReply.id, LikeableType.ACTIVITY_REPLY)
    }

    private fun handleLike(id: Int, likeableType: LikeableType) {
        _loading.onNext(true)

        disposables.add(
            socialRepository.toggleLike(id, likeableType)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    {
                        val currentSocialItems = _socialItemList.value ?: listOf()
                        val editedActivityIndex = if (likeableType == LikeableType.ACTIVITY)
                            0
                        else
                            currentSocialItems.indexOfFirst { it.activityReply?.id == id }

                        if (editedActivityIndex != -1) {
                            if (likeableType == LikeableType.ACTIVITY) {
                                currentSocialItems[editedActivityIndex].activity?.let {
                                    val isNowLiked = !it.isLiked
                                    val likeUsers = ArrayList(it.likes)
                                    if (isNowLiked) likeUsers.add(viewer) else likeUsers.removeAll { it.id == viewer.id }
                                    currentSocialItems[editedActivityIndex].activity?.isLiked = isNowLiked
                                    currentSocialItems[editedActivityIndex].activity?.likeCount = if (isNowLiked) it.likeCount + 1 else it.likeCount - 1
                                    currentSocialItems[editedActivityIndex].activity?.likes = likeUsers
                                }
                            } else {
                                currentSocialItems[editedActivityIndex].activityReply?.let {
                                    val isNowLiked = !it.isLiked
                                    val likeUsers = ArrayList(it.likes)
                                    if (isNowLiked) likeUsers.add(viewer) else likeUsers.removeAll { it.id == viewer.id }
                                    currentSocialItems[editedActivityIndex].activityReply?.isLiked = isNowLiked
                                    currentSocialItems[editedActivityIndex].activityReply?.likeCount = if (isNowLiked) it.likeCount + 1 else it.likeCount - 1
                                    currentSocialItems[editedActivityIndex].activityReply?.likes = likeUsers
                                }
                            }
                            _socialItemList.onNext(currentSocialItems)

                            currentSocialItems.firstOrNull()?.activity?.let {
                                _activityDetailResult.onNext(it to false)
                            }
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
                        currentSocialItems[0].activity?.isSubscribed = !activity.isSubscribed
                        _socialItemList.onNext(currentSocialItems)
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
                        _success.onNext(R.string.activity_deleted)
                        _activityDetailResult.onNext(activity to true)
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun deleteActivityReply(activityReply: ActivityReply) {
        _loading.onNext(true)

        disposables.add(
            socialRepository.deleteActivityReply(activityReply.id)
                .applyScheduler()
                .doFinally { _loading.onNext(false) }
                .subscribe(
                    {
                        val currentSocialItems = ArrayList(_socialItemList.value ?: listOf())
                        val editedActivityIndex = currentSocialItems.indexOfFirst { it.activityReply?.id == activityReply.id }
                        if (editedActivityIndex != -1) {
                            currentSocialItems[0].activity?.replyCount = currentSocialItems[0].activity?.replyCount?.minus(1) ?: 0
                            currentSocialItems.removeAt(editedActivityIndex)
                            _socialItemList.onNext(currentSocialItems)
                        }

                        _success.onNext(R.string.reply_deleted)
                        currentSocialItems.firstOrNull()?.activity?.let {
                            _activityDetailResult.onNext(it to false)
                        }
                    },
                    {
                        _error.onNext(it.getStringResource())
                    }
                )
        )
    }

    fun setActivityToBeEdited(activity: Activity) {
        socialRepository.updateActivityToBeEdited(activity)
    }

    fun setReplyToBeEdited(activityReply: ActivityReply) {
        socialRepository.updateReplyToBeEdited(activityReply)
    }
}