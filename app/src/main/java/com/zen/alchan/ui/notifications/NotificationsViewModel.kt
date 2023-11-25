package com.zen.alchan.ui.notifications

import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Notification
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import com.zen.alchan.type.NotificationType

class NotificationsViewModel(private val userRepository: UserRepository) : BaseViewModel<Unit>() {

    private val _appSetting = PublishSubject.create<AppSetting>()
    val appSetting: Observable<AppSetting>
        get() = _appSetting

    private val _notificationsAndUnreadCount = BehaviorSubject.createDefault<Pair<List<Notification>, Int>>(Pair(listOf(), 0))
    val notificationsAndUnreadCount: Observable<Pair<List<Notification>, Int>>
        get() = _notificationsAndUnreadCount

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

    private val _notificationTypeList = PublishSubject.create<List<ListItem<List<NotificationType>?>>>()
    val notificationTypeList: Observable<List<ListItem<List<NotificationType>?>>>
        get() = _notificationTypeList

    private var selectedNotificationTypes: List<NotificationType>? = null
    private val notificationTypes = listOf(
        null,
        listOf(NotificationType.AIRING),
        listOf(NotificationType.ACTIVITY_REPLY_LIKE, NotificationType.ACTIVITY_REPLY, NotificationType.ACTIVITY_LIKE, NotificationType.ACTIVITY_MENTION, NotificationType.ACTIVITY_MESSAGE, NotificationType.ACTIVITY_REPLY_SUBSCRIBED),
        listOf(NotificationType.THREAD_LIKE, NotificationType.THREAD_SUBSCRIBED, NotificationType.THREAD_COMMENT_LIKE, NotificationType.THREAD_COMMENT_MENTION, NotificationType.THREAD_COMMENT_REPLY),
        listOf(NotificationType.FOLLOWING),
        listOf(NotificationType.RELATED_MEDIA_ADDITION, NotificationType.MEDIA_DATA_CHANGE, NotificationType.MEDIA_MERGE, NotificationType.MEDIA_DELETION)
    )

    private var hasNextPage = false
    private var currentPage = 0

    private var latestNotificationId: Int? = null
    private var unreadNotificationCount = 0

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                userRepository.getAppSetting()
                    .applyScheduler()
                    .subscribe {
                        _appSetting.onNext(it)
                        loadNotifications()
                    }
            )

            disposables.add(
                userRepository.unreadNotificationCount
                    .applyScheduler()
                    .subscribe {
                        if (it != 0)
                            unreadNotificationCount = it
                    }
            )
        }
    }

    fun reloadData() {
        unreadNotificationCount = 0
        loadNotifications()
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentNotifications = ArrayList(_notificationsAndUnreadCount.value?.first ?: listOf())
            currentNotifications.add(null)
            _notificationsAndUnreadCount.onNext(currentNotifications to getUnreadNotificationCount())

            loadNotifications(true)
        }
    }

    private fun loadNotifications(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        disposables.add(
            userRepository.getNotifications(if (isLoadingNextPage) currentPage + 1 else 1, selectedNotificationTypes, true)
                .zipWith(userRepository.getLastNotificationId()) { notifications, lastNotificationId ->
                    notifications.page.data.firstOrNull()?.let {
                        if (it.id > lastNotificationId)
                            userRepository.setLastNotificationId(it.id)
                    }

                    notifications
                }
                .applyScheduler()
                .doFinally {
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                        _emptyLayoutVisibility.onNext(_notificationsAndUnreadCount.value?.first.isNullOrEmpty())
                    }
                }
                .subscribe(
                    {
                        hasNextPage = it.page.pageInfo.hasNextPage
                        currentPage = it.page.pageInfo.currentPage

                        if (isLoadingNextPage) {
                            val currentNotifications = ArrayList(_notificationsAndUnreadCount.value?.first ?: listOf())
                            currentNotifications.remove(null)
                            currentNotifications.addAll(it.page.data)
                            _notificationsAndUnreadCount.onNext(currentNotifications to getUnreadNotificationCount())
                        } else {
                            _notificationsAndUnreadCount.onNext(it.page.data to getUnreadNotificationCount())
                        }

                        state = State.LOADED
                    },
                    {
                        if (isLoadingNextPage) {
                            val currentNotifications = ArrayList(_notificationsAndUnreadCount.value?.first ?: listOf())
                            currentNotifications.remove(null)
                            _notificationsAndUnreadCount.onNext(currentNotifications to getUnreadNotificationCount())
                        }

                        _error.onNext(it.getStringResource())
                        state = State.ERROR
                    }
                )
        )
    }

    fun updateSelectedNotificationTypes(newNotificationTypes: List<NotificationType>?) {
        selectedNotificationTypes = newNotificationTypes
        reloadData()
    }

    fun loadNotificationTypes() {
        val list = ArrayList<ListItem<List<NotificationType>?>>()
        list.add(ListItem(R.string.all, notificationTypes[0]))
        list.add(ListItem(R.string.airing, notificationTypes[1]))
        list.add(ListItem(R.string.activity, notificationTypes[2]))
        list.add(ListItem(R.string.forum, notificationTypes[3]))
        list.add(ListItem(R.string.follows, notificationTypes[4]))
        list.add(ListItem(R.string.media, notificationTypes[5]))
        _notificationTypeList.onNext(list)
    }

    private fun getUnreadNotificationCount(): Int {
        return if (selectedNotificationTypes == null)
            unreadNotificationCount
        else
            0
    }
}