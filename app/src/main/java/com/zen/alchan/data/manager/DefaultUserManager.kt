package com.zen.alchan.data.manager

import com.zen.alchan.data.localstorage.SharedPreferencesHandler
import io.reactivex.Observable

class DefaultUserManager(private val sharedPreferencesManager: SharedPreferencesHandler) : UserManager {

    override var bearerToken: String?
        get() = sharedPreferencesManager.bearerToken
        set(value) { sharedPreferencesManager.bearerToken = value }

    override val isLoggedIn: Boolean = bearerToken != null
}