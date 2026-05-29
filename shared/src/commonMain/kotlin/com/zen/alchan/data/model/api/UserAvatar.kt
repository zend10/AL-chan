package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class UserAvatar(
    val large: String = "",
    val medium: String = ""
)
