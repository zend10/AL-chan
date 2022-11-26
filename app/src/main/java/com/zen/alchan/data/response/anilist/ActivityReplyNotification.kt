package com.zen.alchan.data.response.anilist

import type.NotificationType

data class ActivityReplyNotification(
    override val id: Int = 0,
    val userId: Int = 0,
    override val type: NotificationType = NotificationType.ACTIVITY_REPLY,
    val activityId: Int = 0,
    val context: String = "",
    override val createdAt: Int = 0,
    val user: User = User()
) : Notification