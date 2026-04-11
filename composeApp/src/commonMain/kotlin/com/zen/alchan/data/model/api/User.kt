package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val avatar: UserAvatar = UserAvatar(),
    val bannerImage: String = "",
    val about: String = ""
) {
    fun isGuest(): Boolean {
        return id.isBlank()
    }
}