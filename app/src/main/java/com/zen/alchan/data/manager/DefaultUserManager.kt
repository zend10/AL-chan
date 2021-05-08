package com.zen.alchan.data.manager

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.localstorage.JsonStorageHandler
import com.zen.alchan.data.localstorage.SharedPreferencesHandler
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.pojo.SaveItem

class DefaultUserManager(
    private val sharedPreferencesManager: SharedPreferencesHandler,
    private val jsonStorageHandler: JsonStorageHandler
) : UserManager {

    override var bearerToken: String?
        get() = sharedPreferencesManager.bearerToken
        set(value) { sharedPreferencesManager.bearerToken = value }

    override val isAuthenticated: Boolean
        get() = bearerToken != null

    override var isLoggedInAsGuest: Boolean
        get() = sharedPreferencesManager.guestLogin == true
        set(value) { sharedPreferencesManager.guestLogin = value }

    override var appSetting: AppSetting
        get() = sharedPreferencesManager.appSetting ?: AppSetting.EMPTY_APP_SETTING
        set(value) { sharedPreferencesManager.appSetting = value }

    override var viewerData: SaveItem<User>?
        get() = jsonStorageHandler.viewerData
        set(value) { jsonStorageHandler.viewerData = value }

    override var profileData: SaveItem<ProfileData>?
        get() = jsonStorageHandler.profileData
        set(value) { jsonStorageHandler.profileData = value }
}