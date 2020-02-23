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

    override fun setBearerToken(token: String?) {
        localStorage.bearerToken = token
    }

    override fun setViewerData(user: User?) {
        localStorage.viewerData = user
        localStorage.viewerDataLastRetrieved = Utility.getCurrentTimestamp()
    }
}