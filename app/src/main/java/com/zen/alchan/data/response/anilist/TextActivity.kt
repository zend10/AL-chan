package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.type.ActivityType

data class TextActivity(
    override val id: Int = 0,
    val userId: Int = 0,
    override val type: ActivityType = ActivityType.TEXT,
    override var replyCount: Int = 0,
    val text: String = "",
    override val isLocked: Boolean = false,
    override var isSubscribed: Boolean = false,
    override var likeCount: Int = 0,
    override var isLiked: Boolean = false,
    val isPinned: Boolean = false,
    override val siteUrl: String = "",
    override val createdAt: Int = 0,
    val user: User = User(),
    override var replies: List<ActivityReply> = listOf(),
    override var likes: List<User> = listOf()
) : Activity {

    override fun user(): User {
        return user
    }

    override fun hasRecipient(): Boolean {
        return false
    }

    override fun recipient(): User {
        return User()
    }

    override fun isPrivateMessage(): Boolean {
        return false
    }

    override fun message(appSetting: AppSetting): String {
        return text
    }

    override fun hasMedia(): Boolean {
        return false
    }

    override fun media(): Media {
        return Media()
    }

    override fun isEditable(viewer: User?): Boolean {
        return viewer != null && viewer.id == user.id
    }

    override fun isDeletable(viewer: User?): Boolean {
        return viewer != null && viewer.id == user.id
    }

    override fun isReportable(viewer: User?): Boolean {
        return viewer != null && viewer.id != user.id
    }
}