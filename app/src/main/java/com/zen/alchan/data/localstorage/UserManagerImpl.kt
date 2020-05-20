package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.User
import com.zen.alchan.helper.utils.Utility

class UserManagerImpl(private val localStorage: LocalStorage) : UserManager {

    override val bearerToken: String?
        get() = localStorage.bearerToken

    override val viewerData: User?
        get() = localStorage.viewerData

    override val viewerDataLastRetrieved: Long?
        get() = localStorage.viewerDataLastRetrieved

    override val followersCount: Int
        get() = localStorage.followersCount ?: 0

    override val followersCountLastRetrieved: Long?
        get() = localStorage.followersCountLastRetrieved

    override val followingsCount: Int
        get() = localStorage.followingsCount ?: 0

    override val followingsCountLastRetrieved: Long?
        get() = localStorage.followingsCountLastRetrieved

    override fun setBearerToken(token: String?) {
        localStorage.bearerToken = token
    }

    override fun setViewerData(user: User?) {
        localStorage.viewerData = user
        localStorage.viewerDataLastRetrieved = Utility.getCurrentTimestamp()
    }

    override fun setFollowersCount(followers: Int) {
        localStorage.followersCount = followers
        localStorage.followersCountLastRetrieved = Utility.getCurrentTimestamp()
    }

    override fun setFollowingsCount(followings: Int) {
        localStorage.followingsCount = followings
        localStorage.followingsCountLastRetrieved = Utility.getCurrentTimestamp()
    }
}