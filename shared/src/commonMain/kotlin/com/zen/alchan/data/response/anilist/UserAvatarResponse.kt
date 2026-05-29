package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.UserAvatar
import kotlinx.serialization.Serializable

@Serializable
data class UserAvatarResponse(
    val large: String? = null,
    val medium: String? = null
) {
    fun toModel(): UserAvatar {
        return UserAvatar(
            large = large ?: "",
            medium = medium ?: ""
        )
    }
}
