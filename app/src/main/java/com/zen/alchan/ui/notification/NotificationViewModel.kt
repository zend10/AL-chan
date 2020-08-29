package com.zen.alchan.ui.notification

import androidx.lifecycle.ViewModel
import com.zen.alchan.R
import com.zen.alchan.data.repository.UserRepository
import type.NotificationType

class NotificationViewModel(private val userRepository: UserRepository) : ViewModel() {

    var selectedTypes: List<NotificationType>? = null

    val notificationTypesArray = arrayOf(
        R.string.all,
        R.string.airing,
        R.string.activity,
        R.string.forum,
        R.string.follows,
        R.string.relations
    )

    val notificationTypesList = arrayListOf(
        null,
        listOf(NotificationType.AIRING),
        listOf(NotificationType.ACTIVITY_REPLY_LIKE, NotificationType.ACTIVITY_REPLY, NotificationType.ACTIVITY_LIKE, NotificationType.ACTIVITY_MENTION, NotificationType.ACTIVITY_MESSAGE, NotificationType.ACTIVITY_REPLY_SUBSCRIBED),
        listOf(NotificationType.THREAD_LIKE, NotificationType.THREAD_SUBSCRIBED, NotificationType.THREAD_COMMENT_LIKE, NotificationType.THREAD_COMMENT_MENTION, NotificationType.THREAD_COMMENT_REPLY),
        listOf(NotificationType.FOLLOWING),
        listOf(NotificationType.RELATED_MEDIA_ADDITION)
    )

    var page = 1
    var hasNextPage = true
    var isInit = false

    var notificationList = ArrayList<NotificationsQuery.Notification?>()

    val notificationsResponse by lazy {
        userRepository.notificationsResponse
    }

    val unreadNotifications: Int
        get() = userRepository.currentUser?.unreadNotificationCount ?: 0

    fun getNotifications() {
        if (hasNextPage) {
            userRepository.getNotifications(page, selectedTypes, true)
        }
    }
}