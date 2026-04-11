package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.User
import com.zen.alchan.data.model.api.UserAvatar
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int? = null,
    val name: String? = null,
    val avatar: UserAvatarResponse? = null,
    val bannerImage: String? = null,
    val about: String? = null
) {
    fun toModel(): User {
        return User(
            id = id?.toString() ?: "",
            name = name ?: "",
            avatar = UserAvatar(
                large = avatar?.large ?: "",
                medium = avatar?.medium ?: ""
            ),
            bannerImage = bannerImage ?: "",
            about = about ?: ""
        )
    }
}
