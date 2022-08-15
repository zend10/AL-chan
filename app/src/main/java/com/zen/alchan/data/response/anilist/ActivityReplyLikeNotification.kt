package com.zen.alchan.data.response.anilist

import type.NotificationType

data class ActivityReplyLikeNotification(
    override val id: Int = 0,
    val userId: Int = 0,
    override val type: NotificationType = NotificationType.ACTIVITY_REPLY_LIKE,
    val activityId: Int = 0,
    val context: String = "",
    override val createdAt: Int = 0,
    val user: User = User()
) : Notification