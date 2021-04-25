package com.zen.alchan.data.manager

import com.zen.alchan.data.localstorage.JsonStorageHandler
import com.zen.alchan.data.localstorage.SharedPreferencesHandler
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.pojo.SaveItem
import io.reactivex.Observable

class DefaultUserManager(
    private val sharedPreferencesManager: SharedPreferencesHandler,
    private val jsonStorageHandler: JsonStorageHandler
) : UserManager {

    override var bearerToken: String?
        get() = sharedPreferencesManager.bearerToken
        set(value) { sharedPreferencesManager.bearerToken = value }

    override val isLoggedIn: Boolean
        get() = bearerToken != null

    override var isLoggedInAsGuest: Boolean
        get() = sharedPreferencesManager.guestLogin == true
        set(value) { sharedPreferencesManager.guestLogin = value }

    override var viewerData: SaveItem<User>?
        get() = jsonStorageHandler.viewerData
        set(value) { jsonStorageHandler.viewerData = value }
}