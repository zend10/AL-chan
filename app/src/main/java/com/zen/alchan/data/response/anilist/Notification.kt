package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.type.NotificationType

interface Notification {
    val id: Int
    val type: NotificationType
    val createdAt: Int
    fun getMessage(appSetting: AppSetting): String
}