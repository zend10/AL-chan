package com.zen.alchan.data.response

class User(
    val id: Int,
    val name: String,
    val about: String? = null,
    val avatar: UserAvatar? = null,
    val bannerImage: String? = null,
    var options: UserOptions? = null,
    var mediaListOptions: MediaListOptions? = null,
    val statistics: UserStatisticTypes? = null,
    var unreadNotificationCount: Int? = null,
    val donatorTier: Int? = null,
    val donatorBadge: String? = null,
    val moderatorStatus: String? = null,
    val siteUrl: String? = null
)