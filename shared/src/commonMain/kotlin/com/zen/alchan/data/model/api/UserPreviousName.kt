package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class UserPreviousName(
    val name: String = "",
    val createdAt: Int = 0,
    val updatedAt: Int = 0
)
