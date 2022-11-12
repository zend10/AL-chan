package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
import type.ActivityType
import java.util.*

data class ListActivity(
    override val id: Int = 0,
    val userId: Int = 0,
    override val type: ActivityType = ActivityType.MEDIA_LIST,
    override val replyCount: Int = 0,
    val status: String = "",
    val progress: String = "",
    override val isLocked: Boolean = false,
    override var isSubscribed: Boolean = false,
    override var likeCount: Int = 0,
    override var isLiked: Boolean = false,
    val isPinned: Boolean = false,
    override val siteUrl: String = "",
    override val createdAt: Int = 0,
    val user: User = User(),
    val media: Media = Media(),
    override val replies: List<ActivityReply> = listOf(),
    override val likes: List<User> = listOf()
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

    override fun message(appSetting: AppSetting): String {
        return "$status $progress ${media.getTitle(appSetting)}".replaceFirstChar {
            if (it.isLowerCase())
                it.titlecase(Locale.getDefault())
            else
                it.toString()
        }
    }

    override fun hasMedia(): Boolean {
        return true
    }

    override fun media(): Media {
        return media
    }

    override fun isEditable(viewer: User?): Boolean {
        return false
    }

    override fun isDeletable(viewer: User?): Boolean {
        return viewer != null && viewer.id == user.id
    }

    override fun isReportable(viewer: User?): Boolean {
        return false
    }
}