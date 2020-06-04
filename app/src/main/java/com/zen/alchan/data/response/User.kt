package com.zen.alchan.data.response

class User(
    val id: Int,
    val name: String,
    val about: String?,
    val avatar: UserAvatar?,
    val bannerImage: String?,
    var options: UserOptions?,
    var mediaListOptions: MediaListOptions?,
    val statistics: UserStatisticTypes?,
    var unreadNotificationCount: Int?,
    val donatorTier: Int?,
    val donatorBadge: String?,
    val moderatorStatus: String?,
    val siteUrl: String?
)