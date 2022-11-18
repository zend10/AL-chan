package com.zen.alchan.data.response.anilist

data class ActivityReply(
    val id: Int = 0,
    val userId: Int = 0,
    val activityId: Int = 0,
    val text: String = "",
    var likeCount: Int = 0,
    var isLiked: Boolean = false,
    val createdAt: Int = 0,
    val user: User = User(),
    var likes: List<User> = listOf()
)