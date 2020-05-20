package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.User

interface UserManager {
    val bearerToken: String?
    val viewerData: User?
    val viewerDataLastRetrieved: Long?
    val followersCount: Int
    val followersCountLastRetrieved: Long?
    val followingsCount: Int
    val followingsCountLastRetrieved: Long?

    fun setBearerToken(token: String?)
    fun setViewerData(user: User?)
    fun setFollowersCount(followers: Int)
    fun setFollowingsCount(followings: Int)
}