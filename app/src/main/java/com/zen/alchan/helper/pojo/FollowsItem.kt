package com.zen.alchan.helper.pojo

class FollowsItem(
    val id: Int,
    val name: String,
    val image: String?,
    var isFollowing: Boolean,
    var isFollower: Boolean,
    val siteUrl: String
)