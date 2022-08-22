package com.zen.alchan.ui.notifications

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.anilist.Notification
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.helper.extensions.getStringResource
import com.zen.alchan.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import type.NotificationType

class NotificationsViewModel(private val userRepository: UserRepository) : BaseViewModel<Unit>() {

    private val _appSetting = PublishSubject.create<AppSetting>()
    val appSetting: Observable<AppSetting>
        get() = _appSetting

    private val _notifications = BehaviorSubject.createDefault<List<Notification>>(listOf())
    val notifications: Observable<List<Notification>>
        get() = _notifications

    private val _emptyLayoutVisibility = BehaviorSubject.createDefault(false)
    val emptyLayoutVisibility: Observable<Boolean>
        get() = _emptyLayoutVisibility

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
        }
    }

    fun reloadData() {
        loadNotifications()
    }

    fun loadNextPage() {
        if ((state == State.LOADED || state == State.ERROR) && hasNextPage) {
            val currentNotifications = ArrayList(_notifications.value ?: listOf())
            currentNotifications.add(null)
            _notifications.onNext(currentNotifications)

            loadNotifications(true)
        }
    }

    private fun loadNotifications(isLoadingNextPage: Boolean = false) {
        if (!isLoadingNextPage)
            _loading.onNext(true)

        state = State.LOADING

        disposables.add(
            userRepository.getNotifications(if (isLoadingNextPage) currentPage + 1 else 1, selectedNotificationTypes, true)
                .applyScheduler()
                .doFinally {
                    if (!isLoadingNextPage) {
                        _loading.onNext(false)
                        _emptyLayoutVisibility.onNext(_notifications.value.isNullOrEmpty())
                    }
                }
                .subscribe(
                    {
                        hasNextPage = it.page.pageInfo.hasNextPage
                        currentPage = it.page.pageInfo.currentPage

                        if (isLoadingNextPage) {
                            val currentNotifications = ArrayList(_notifications.value ?: listOf())
                            currentNotifications.remove(null)
                            currentNotifications.addAll(it.page.data)
                            _notifications.onNext(currentNotifications)
                        } else {
                            _notifications.onNext(it.page.data)
                        }

                        state = State.LOADED
                    },
                    {
                        if (isLoadingNextPage) {
                            val currentNotifications = ArrayList(_notifications.value ?: listOf())
                            currentNotifications.remove(null)
                            _notifications.onNext(currentNotifications)
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

    }
}