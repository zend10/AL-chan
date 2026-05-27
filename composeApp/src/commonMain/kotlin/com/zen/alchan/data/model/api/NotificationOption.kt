package com.zen.alchan.data.model.api

import com.zen.alchan.data.enums.NotificationType
import kotlinx.serialization.Serializable

@Serializable
data class NotificationOption(
    val type: NotificationType? = null,
    val enabled: Boolean = false
)
