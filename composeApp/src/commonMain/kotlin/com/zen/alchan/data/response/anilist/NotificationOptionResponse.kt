package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.enums.getNotificationTypeEnum
import com.zen.alchan.data.model.api.NotificationOption
import kotlinx.serialization.Serializable

@Serializable
data class NotificationOptionResponse(
    val type: String? = null,
    val enabled: Boolean? = null
) {
    fun toModel(): NotificationOption {
        return NotificationOption(
            type = getNotificationTypeEnum(type),
            enabled = enabled ?: false
        )
    }
}
