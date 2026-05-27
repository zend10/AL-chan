package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.UserPreviousName
import kotlinx.serialization.Serializable

@Serializable
data class UserPreviousNameResponse(
    val name: String? = null,
    val createdAt: Int? = null,
    val updatedAt: Int? = null
) {
    fun toModel(): UserPreviousName {
        return UserPreviousName(
            name = name ?: "",
            createdAt = createdAt ?: 0,
            updatedAt = updatedAt ?: 0
        )
    }
}
