package com.zen.alchan.data.response

data class User(
    val id: Int = 0,
    val name: String = "",
    val about: String = "",
    val avatar: UserAvatar = UserAvatar(),
    val bannerImage: String = "",
    val options: UserOptions = UserOptions(),
    val mediaListOptions: MediaListOptions = MediaListOptions(),
//    val favourites: Favourites,
    val statistics: UserStatisticTypes = UserStatisticTypes(),
    val unreadNotificationCount: Int = 0,
    val siteUrl: String = "",
    val donatorTier: Int = 0,
    val donatorBadge: String = "",
    val moderatorStatus: String = ""
) {
    companion object {
        val EMPTY_USER = User()
    }
}