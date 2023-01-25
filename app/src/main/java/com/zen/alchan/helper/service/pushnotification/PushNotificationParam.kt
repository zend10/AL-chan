package com.zen.alchan.helper.service.pushnotification

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Notification

data class PushNotificationParam(
    val notifications: List<Notification>,
    val unreadNotificationCount: Int,
    val appSetting: AppSetting,
    val lastNotificationId: Int
)
