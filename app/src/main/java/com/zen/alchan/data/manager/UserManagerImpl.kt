package com.zen.alchan.data.manager

import com.zen.alchan.data.localstorage.SharedPreferencesManager

class UserManagerImpl(private val sharedPreferencesManager: SharedPreferencesManager) : UserManager {

    override var bearerToken: String?
        get() = sharedPreferencesManager.bearerToken
        set(value) { sharedPreferencesManager.bearerToken = value }

    override val isLoggedIn: Boolean = bearerToken != null
}