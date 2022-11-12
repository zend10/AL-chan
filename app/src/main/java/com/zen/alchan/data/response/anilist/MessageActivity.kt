package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
import type.ActivityType

data class MessageActivity(
    override val id: Int = 0,
    val recipientId: Int = 0,
    val messengerId: Int = 0,
    override val type: ActivityType = ActivityType.MESSAGE,
    override val replyCount: Int = 0,
    val message: String = "",
    override val isLocked: Boolean = false,
    override var isSubscribed: Boolean = false,
    override var likeCount: Int = 0,
    override var isLiked: Boolean = false,
    val isPrivate: Boolean = false,
    override val siteUrl: String = "",
    override val createdAt: Int = 0,
    val recipient: User = User(),
    val messenger: User = User(),
    override val replies: List<ActivityReply> = listOf(),
    override val likes: List<User> = listOf()
) : Activity {

    override fun user(): User {
        return messenger
    }

    override fun hasRecipient(): Boolean {
        return true
    }

    override fun recipient(): User {
        return recipient
    }

    override fun message(appSetting: AppSetting): String {
        return message
    }

    override fun hasMedia(): Boolean {
        return false
    }

    override fun media(): Media {
        return Media()
    }

    override fun isEditable(viewer: User?): Boolean {
        return viewer != null && viewer.id == messenger.id
    }

    override fun isDeletable(viewer: User?): Boolean {
        return viewer != null && (viewer.id == messenger.id || viewer.id == recipient.id)
    }

    override fun isReportable(viewer: User?): Boolean {
        return viewer != null && viewer.id != messenger.id
    }
}