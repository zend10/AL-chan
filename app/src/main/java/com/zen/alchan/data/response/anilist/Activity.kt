package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.entity.AppSetting
import type.ActivityType

interface Activity {
    val id: Int
    val type: ActivityType
    val replyCount: Int
    val isLocked: Boolean
    var isSubscribed: Boolean
    var likeCount: Int
    var isLiked: Boolean
    val siteUrl: String
    val createdAt: Int
    val replies: List<ActivityReply>
    val likes: List<User>

    fun user(): User
    fun hasRecipient(): Boolean
    fun recipient(): User
    fun message(appSetting: AppSetting): String
    fun hasMedia(): Boolean
    fun media(): Media
}