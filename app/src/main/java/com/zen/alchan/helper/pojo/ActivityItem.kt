package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.User
import type.ActivityType

abstract class ActivityItem(
    val id: Int,
    val type: ActivityType?,
    var replyCount: Int,
    val siteUrl: String?,
    var isSubscribed: Boolean?,
    var likeCount: Int,
    var isLiked: Boolean?,
    val createdAt: Int,
    var replies: List<ActivityReply>?,
    var likes: List<User>?
)