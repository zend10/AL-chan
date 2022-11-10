package com.zen.alchan.data.response.anilist

data class ActivityReply(
    val id: Int = 0,
    val userId: Int = 0,
    val activityId: Int = 0,
    val text: String = "",
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val createdAt: Int = 0,
    val user: User = User(),
    val likes: List<User> = listOf()
)