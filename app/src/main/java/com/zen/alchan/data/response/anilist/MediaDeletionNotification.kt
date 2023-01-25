package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
import type.NotificationType

data class MediaDeletionNotification(
    override val id: Int = 0,
    override val type: NotificationType = NotificationType.MEDIA_DELETION,
    val deletedMediaTitle: String = "",
    val context: String = "",
    val reason: String = "",
    override val createdAt: Int = 0
) : Notification {
    override fun getMessage(appSetting: AppSetting): String {
        return "${deletedMediaTitle}${context}"
    }
}