package com.zen.alchan.data.response.anilist

import type.NotificationType

data class NotificationOption(
    val type: NotificationType? = null,
    val enabled: Boolean = false
)