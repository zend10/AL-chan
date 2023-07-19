package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.type.ActivityType

interface Activity {
    val id: Int
    val type: ActivityType
    var replyCount: Int
    val isLocked: Boolean
    var isSubscribed: Boolean
    var likeCount: Int
    var isLiked: Boolean
    val siteUrl: String
    val createdAt: Int
    var replies: List<ActivityReply>
    var likes: List<User>

    fun user(): User
    fun hasRecipient(): Boolean
    fun recipient(): User
    fun isPrivateMessage(): Boolean
    fun message(appSetting: AppSetting): String
    fun hasMedia(): Boolean
    fun media(): Media
    fun isEditable(viewer: User?): Boolean
    fun isDeletable(viewer: User?): Boolean
    fun isReportable(viewer: User?): Boolean
}