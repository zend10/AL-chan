package com.zen.alchan.data.response

data class User(
    val id: Int = 0,
    val name: String = "",
    val about: String = "",
    val avatar: UserAvatar = UserAvatar()
)