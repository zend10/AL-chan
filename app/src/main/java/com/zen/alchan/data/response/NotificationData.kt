package com.zen.alchan.data.response

import com.zen.alchan.data.response.anilist.Notification
import com.zen.alchan.data.response.anilist.Page

data class NotificationData(
    val page: Page<Notification> = Page(),
    val unreadNotificationCount: Int = 0
)