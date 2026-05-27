package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.enums.getModRoleEnum
import com.zen.alchan.data.model.api.MediaListOptions
import com.zen.alchan.data.model.api.User
import com.zen.alchan.data.model.api.UserAvatar
import com.zen.alchan.data.model.api.UserOptions
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int? = null,
    val name: String? = null,
    val avatar: UserAvatarResponse? = null,
    val bannerImage: String? = null,
    val about: String? = null,
    val isFollowing: Boolean? = null,
    val isFollower: Boolean? = null,
    val isBlocked: Boolean? = null,
    val options: UserOptionsResponse? = null,
    val mediaListOptions: MediaListOptionsResponse? = null,
    val unreadNotificationCount: Int? = null,
    val siteUrl: String? = null,
    val donatorTier: Int? = null,
    val donatorBadge: String? = null,
    val moderatorRoles: List<String>? = null,
    val createdAt: Int? = null,
    val updatedAt: Int? = null,
    val previousNames: List<UserPreviousNameResponse>? = null
) {
    fun toModel(): User {
        return User(
            id = id?.toString() ?: "",
            name = name ?: "",
            avatar = avatar?.toModel() ?: UserAvatar(),
            bannerImage = bannerImage ?: "",
            about = about ?: "",
            isFollowing = isFollowing ?: false,
            isFollower = isFollower ?: false,
            isBlocked = isBlocked ?: false,
            options = options?.toModel() ?: UserOptions(),
            mediaListOptions = mediaListOptions?.toModel() ?: MediaListOptions(),
            unreadNotificationCount = unreadNotificationCount ?: 0,
            siteUrl = siteUrl ?: "",
            donatorTier = donatorTier ?: 0,
            donatorBadge = donatorBadge ?: "",
            moderatorRoles = moderatorRoles?.mapNotNull { getModRoleEnum(it) } ?: listOf(),
            createdAt = createdAt ?: 0,
            updatedAt = updatedAt ?: 0,
            previousNames = previousNames?.map { it.toModel() } ?: listOf()
        )
    }
}
