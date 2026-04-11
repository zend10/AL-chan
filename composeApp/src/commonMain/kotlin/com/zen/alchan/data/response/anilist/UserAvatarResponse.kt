package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class UserAvatarResponse(
    val large: String? = null,
    val medium: String? = null
)
