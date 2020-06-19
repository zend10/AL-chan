package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.User
import type.ActivityType

class MessageActivity(
    id: Int,
    type: ActivityType?,
    replyCount: Int,
    siteUrl: String?,
    isSubscribed: Boolean?,
    likeCount: Int,
    isLiked: Boolean?,
    createdAt: Int,
    replies: ArrayList<ActivityReply>?,
    likes: List<User>?,
    val recipientId: Int?,
    val messengerId: Int?,
    var message: String?,
    var isPrivate: Boolean?,
    val recipient: User?,
    val messenger: User?
) : ActivityItem(
    id,
    type,
    replyCount,
    siteUrl,
    isSubscribed,
    likeCount,
    isLiked,
    createdAt,
    replies,
    likes
)