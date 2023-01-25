package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
import type.NotificationType

data class FollowingNotification(
    override val id: Int = 0,
    val userId: Int = 0,
    override val type: NotificationType = NotificationType.FOLLOWING,
    val context: String = "",
    override val createdAt: Int = 0,
    val user: User = User()
) : Notification {
    override fun getMessage(appSetting: AppSetting): String {
        return "${user.name}${context}"
    }
}