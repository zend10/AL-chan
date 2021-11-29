package com.zen.alchan.data.manager

import android.net.Uri
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.data.localstorage.JsonStorageHandler
import com.zen.alchan.data.localstorage.SharedPreferencesHandler
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.data.localstorage.FileStorageHandler
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.pojo.SaveItem
import io.reactivex.Observable

class DefaultUserManager(
    private val sharedPreferencesManager: SharedPreferencesHandler,
    private val jsonStorageHandler: JsonStorageHandler,
    private val fileStorageHandler: FileStorageHandler
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

    override val animeListBackground: Observable<NullableItem<Uri>>
        get() = fileStorageHandler.animeListBackground

    override val mangaListBackground: Observable<NullableItem<Uri>>
        get() = fileStorageHandler.mangaListBackground

    override fun saveAnimeListBackground(uri: Uri?): Observable<Unit> {
        return fileStorageHandler.saveAnimeListBackground(uri)
    }

    override fun saveMangaListBackground(uri: Uri?): Observable<Unit> {
        return fileStorageHandler.saveMangaListBackground(uri)
    }

    override var viewerData: SaveItem<User>?
        get() = jsonStorageHandler.viewerData
        set(value) { jsonStorageHandler.viewerData = value }

    override var followingCount: Int?
        get() = sharedPreferencesManager.followingCount
        set(value) { sharedPreferencesManager.followingCount = value }

    override var followersCount: Int?
        get() = sharedPreferencesManager.followersCount
        set(value) { sharedPreferencesManager.followersCount = value }

    override var animeList: SaveItem<MediaListCollection>?
        get() = jsonStorageHandler.animeList
        set(value) { jsonStorageHandler.animeList = value }

    override var mangaList: SaveItem<MediaListCollection>?
        get() = jsonStorageHandler.mangaList
        set(value) { jsonStorageHandler.mangaList = value }
}