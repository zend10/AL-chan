package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.User

interface UserManager {
    val bearerToken: String?
    val viewerData: User?
    val viewerDataLastRetrieved: Long?

    fun setBearerToken(token: String?)
    fun setViewerData(user: User?)
}