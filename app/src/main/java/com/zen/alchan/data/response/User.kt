package com.zen.alchan.data.response

class User(
    val id: Int,
    val name: String,
    val about: String?,
    val avatar: UserAvatar?,
    val bannerImage: String?,
    val options: UserOptions?,
    val mediaListOptions: MediaListOptions?,
    val statistics: UserStatisticTypes?,
    val unreadNotificationCount: Int?,
    val siteUrl: String?
)