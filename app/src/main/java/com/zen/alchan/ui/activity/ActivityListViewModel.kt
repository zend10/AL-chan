package com.zen.alchan.ui.activity

import com.zen.alchan.R
import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.ActivityListPage
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.SocialAdapterComponent
import com.zen.alchan.helper.pojo.SocialItem
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.ActivityType
import type.LikeableType

class ActivityListViewModel(
    private val socialRepository: SocialRepository,
    private val userRepository: UserRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel<ActivityListParam>() {

    private val _adapterComponent = PublishSubject.create<SocialAdapterComponent>()
    val adapterComponent: Observable<SocialAdapterComponent>
        get() = _adapterComponent

    private val _socialItemList = BehaviorSubject.createDefault(listOf<SocialItem?>())
    val socialItemList: Observable<List<SocialItem?>>
        get() = _socialItemList

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private val _activityTypeList = PublishSubject.create<Pair<List<ListItem<ActivityType>>, ArrayList<Int>>>()
    val activityTypeList: Observable<Pair<List<ListItem<ActivityType>>, ArrayList<Int>>>
        get() = _activityTypeList

    private val _toolbarTitle = BehaviorSubject.createDefault(R.string.activity_list)
    val toolbarTitle: Observable<Int>
        get() = _toolbarTitle

    private val activityTypes = listOf(
        ActivityType.TEXT,
        ActivityType.MESSAGE,
        ActivityType.MEDIA_LIST
    )

    private var hasNextPage = false
    private var currentPage = 0

    private var activityListPage = ActivityListPage.SPECIFIC_USER
    private var userId: Int = 0
    private var viewer = User()
    private var selectedActivityTypes: ArrayList<ActivityType>? = null
    private var isFollowing: Boolean? = null

    override fun loadData(param: ActivityListParam) {
        loadOnce {
            activityListPage = param.activityListPage
            userId = param.userId
            when (activityListPage) {
                ActivityListPage.FRIENDS -> {
                    selectedActivityTypes = arrayListOf(ActivityType.TEXT, ActivityType.ANIME_LIST, ActivityType.MANGA_LIST)
                    isFollowing = true
                    _toolbarTitle.onNext(R.string.friends_recent_activities)
                }
                ActivityListPage.GLOBAL -> {
                    selectedActivityTypes = arrayListOf(ActivityType.TEXT)
                    _toolbarTitle.onNext(R.string.global_activity)
                }
            }

            disposables.add(
                userRepository.getAppSetting()
                    .zipWith(userRepository.getViewer(Source.CACHE).onErrorReturn { null }) { appSetting, viewer ->
                        this.viewer = viewer
                        SocialAdapterComponent(viewer = viewer, appSetting = appSetting)
                    }
                    .applyScheduler()
                    .subscribe {
                        _adapterComponent.onNext(it)
                        loadActivities()
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
                        currentActivities.add(0, SocialItem(activity, viewType = SocialItem.VIEW_TYPE_ACTIVITY))
                        _socialItemList.onNext(currentActivities)
                    }
                }
        )
    }

    fun reloadData() {
        loadActivities()
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentSocialItems = ArrayList(_socialItemList.value ?: listOf())
            currentSocialItems.add(null)
            _socialItemList.onNext(currentSocialItems)

            loadActivities(true)
        }
    }

    private fun loadActivities(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        disposables.add(
            socialRepository.getActivityList(
                if (isLoadingNextPage) currentPage + 1 else 1,
                if (userId != 0) userId else null,
                selectedActivityTypes,
                isFollowing
            )
                .applyScheduler()
                .doFinally {
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                        _emptyLayoutVisibility.onNext(_socialItemList.value.isNullOrEmpty())
                    }
                }
                .subscribe(
                    {
                        hasNextPage = it.pageInfo.hasNextPage
                        currentPage = it.pageInfo.currentPage

                        if (isLoadingNextPage) {
                            val currentSocialItems = ArrayList(_socialItemList.value ?: listOf())
                            currentSocialItems.remove(null)
                            currentSocialItems.addAll(it.data.map {
                                SocialItem(activity = it, viewType = SocialItem.VIEW_TYPE_ACTIVITY)
                            })
                            _socialItemList.onNext(currentSocialItems)
                        } else {
                            _socialItemList.onNext(it.data.map {
                                SocialItem(activity = it, viewType = SocialItem.VIEW_TYPE_ACTIVITY)
                            })
                        }

                        state = State.LOADED
                    },
                    {
                        if (isLoadingNextPage) {
                            val currentSocialItems = ArrayList(_socialItemList.value ?: listOf())
                            currentSocialItems.remove(null)
                            _socialItemList.onNext(currentSocialItems)
                        }

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
                        val editedActivityIndex = currentSocialItems.indexOfFirst { it?.activity?.id == activity.id }
                        if (editedActivityIndex != -1) {
                            val isNowLiked = !activity.isLiked
                            val likeUsers = ArrayList(activity.likes)
                            if (isNowLiked) likeUsers.add(viewer) else likeUsers.removeAll { it.id == viewer.id }
                            currentSocialItems[editedActivityIndex]?.activity?.isLiked = isNowLiked
                            currentSocialItems[editedActivityIndex]?.activity?.likeCount = if (isNowLiked) activity.likeCount + 1 else activity.likeCount - 1
                            currentSocialItems[editedActivityIndex]?.activity?.likes = likeUsers
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
                        val editedActivityIndex = currentSocialItems.indexOfFirst { it?.activity?.id == activity.id }
                        if (editedActivityIndex != -1) {
                            currentSocialItems[editedActivityIndex]?.activity?.isSubscribed = !activity.isSubscribed
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
                            val editedActivityIndex = currentSocialItems.indexOfFirst { it?.activity?.id == activity.id }
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
                val editedActivityIndex = currentSocialItems.indexOfFirst { it?.activity?.id == activity.id }
                if (editedActivityIndex != -1) {
                    currentSocialItems.removeAt(editedActivityIndex)
                    _socialItemList.onNext(currentSocialItems)
                }
            }
        } else {
            val editedActivityIndex = currentSocialItems.indexOfFirst { it?.activity?.id == activity.id }
            if (editedActivityIndex != -1) {
                currentSocialItems.removeAt(editedActivityIndex)
                currentSocialItems.add(editedActivityIndex, SocialItem(activity = activity, viewType = SocialItem.VIEW_TYPE_ACTIVITY))
                _socialItemList.onNext(currentSocialItems)
            }
        }
    }

    fun loadActivityTypeList() {
        val items = ArrayList<ListItem<ActivityType>>()
        items.addAll(activityTypes.filterNotNull().map { ListItem(it.getString(), it) })
        val selectedIndex = ArrayList<Int>()
        if (selectedActivityTypes?.contains(ActivityType.TEXT) == true)
            selectedIndex.add(0)
        if (selectedActivityTypes?.contains(ActivityType.MESSAGE) == true)
            selectedIndex.add(1)
        if (selectedActivityTypes?.contains(ActivityType.ANIME_LIST) == true || selectedActivityTypes?.contains(ActivityType.MANGA_LIST) == true)
            selectedIndex.add(2)
        _activityTypeList.onNext(items to selectedIndex)
    }

    fun updateSelectedActivityTypes(newSelectedActivityTypes: List<ActivityType>) {
        selectedActivityTypes = if (newSelectedActivityTypes.isEmpty())
            null
        else
            ArrayList<ActivityType>()

        if (newSelectedActivityTypes.contains(ActivityType.TEXT))
            selectedActivityTypes?.add(ActivityType.TEXT)
        if (newSelectedActivityTypes.contains(ActivityType.MESSAGE))
            selectedActivityTypes?.add(ActivityType.MESSAGE)
        if (newSelectedActivityTypes.contains(ActivityType.MEDIA_LIST))
            selectedActivityTypes?.addAll(listOf(ActivityType.ANIME_LIST, ActivityType.MANGA_LIST))

        reloadData()
    }
}