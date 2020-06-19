package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.User
import type.ActivityType

open class ActivityItem(
    val id: Int,
    val type: ActivityType? = null,
    var replyCount: Int = 0,
    val siteUrl: String? = null,
    var isSubscribed: Boolean? = null,
    var likeCount: Int = 0,
    var isLiked: Boolean? = null,
    val createdAt: Int = 0,
    var replies: ArrayList<ActivityReply>? = null,
    var likes: List<User>? = null
)