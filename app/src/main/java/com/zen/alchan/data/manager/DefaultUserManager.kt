package com.zen.alchan.data.manager

import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.data.localstorage.JsonStorageHandler
import com.zen.alchan.data.localstorage.SharedPreferencesHandler
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.entitiy.ListStyle
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

    override var animeListStyle: ListStyle
        get() = sharedPreferencesManager.animeListStyle ?: ListStyle()
        set(value) { sharedPreferencesManager.animeListStyle = value }

    override var mangaListStyle: ListStyle
        get() = sharedPreferencesManager.mangaListStyle ?: ListStyle()
        set(value) { sharedPreferencesManager.mangaListStyle = value }

    override var animeFilter: MediaFilter
        get() = sharedPreferencesManager.animeFilter ?: MediaFilter()
        set(value) { sharedPreferencesManager.animeFilter = value }

    override var mangaFilter: MediaFilter
        get() = sharedPreferencesManager.mangaFilter ?: MediaFilter()
        set(value) { sharedPreferencesManager.mangaFilter = value }

    override var appSetting: AppSetting
        get() = sharedPreferencesManager.appSetting ?: AppSetting()
        set(value) { sharedPreferencesManager.appSetting = value }

    override var viewerData: SaveItem<User>?
        get() = jsonStorageHandler.viewerData
        set(value) { jsonStorageHandler.viewerData = value }

    override var profileData: SaveItem<ProfileData>?
        get() = jsonStorageHandler.profileData
        set(value) { jsonStorageHandler.profileData = value }
}