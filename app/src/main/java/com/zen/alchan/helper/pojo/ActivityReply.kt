package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.User

class ActivityReply(
    val id: Int,
    val userId: Int?,
    val activityId: Int?,
    var text: String?,
    val likeCount: Int,
    var isLiked: Boolean?,
    val createdAt: Int,
    val user: User?,
    val likes: List<User>?
)