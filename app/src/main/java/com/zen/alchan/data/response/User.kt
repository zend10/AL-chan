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
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val user = other as User
        return (
            id == user.id &&
            name == user.name &&
            about == user.about &&
            avatar == user.avatar &&
            bannerImage == user.bannerImage &&
            options == user.options &&
            mediaListOptions == user.mediaListOptions &&
            statistics == user.statistics &&
            unreadNotificationCount == user.unreadNotificationCount &&
            donatorTier == user.donatorTier &&
            donatorBadge == user.donatorBadge &&
            moderatorStatus == user.moderatorStatus &&
            siteUrl == user.siteUrl
        )
    }
}