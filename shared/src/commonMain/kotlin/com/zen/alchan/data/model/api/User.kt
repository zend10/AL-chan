package com.zen.alchan.data.model.api

import com.zen.alchan.data.enums.ModRole
import com.zen.alchan.data.model.AppConfig
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val avatar: UserAvatar = UserAvatar(),
    val bannerImage: String = "",
    val about: String = "",
    val isFollowing: Boolean = false,
    val isFollower: Boolean = false,
    val isBlocked: Boolean = false,
    val options: UserOptions = UserOptions(),
    val mediaListOptions: MediaListOptions = MediaListOptions(),
    val unreadNotificationCount: Int = 0,
    val siteUrl: String = "",
    val donatorTier: Int = 0,
    val donatorBadge: String = "",
    val moderatorRoles: List<ModRole> = listOf(),
    val createdAt: Int = 0,
    val updatedAt: Int = 0,
    val previousNames: List<UserPreviousName> = listOf()
) {
    fun isGuest(): Boolean {
        return id.isBlank()
    }

    fun getAvatar(appConfig: AppConfig): String {
        return avatar.large
    }
}
