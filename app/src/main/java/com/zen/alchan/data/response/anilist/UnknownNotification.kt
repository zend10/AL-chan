package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.type.NotificationType

data class UnknownNotification(
    override val id: Int = 0,
    override val type: NotificationType = NotificationType.UNKNOWN__,
    override val createdAt: Int = 0
) : Notification {
    override fun getMessage(appSetting: AppSetting): String {
        return ""
    }
}
