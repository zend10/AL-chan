package com.zen.alchan.data.manager

import android.net.Uri
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.localstorage.JsonStorageHandler
import com.zen.alchan.data.localstorage.SharedPreferencesHandler
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.localstorage.FileStorageHandler
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.pojo.SaveItem
import io.reactivex.Observable

class DefaultUserManager(
    private val sharedPreferencesHandler: SharedPreferencesHandler,
    private val jsonStorageHandler: JsonStorageHandler,
    private val fileStorageHandler: FileStorageHandler
) : UserManager {

    override var bearerToken: String?
        get() = sharedPreferencesHandler.bearerToken
        set(value) { sharedPreferencesHandler.bearerToken = value }

    override val isAuthenticated: Boolean
        get() = bearerToken != null

    override var isLoggedInAsGuest: Boolean
        get() = sharedPreferencesHandler.guestLogin == true
        set(value) { sharedPreferencesHandler.guestLogin = value }

    override var animeListStyle: ListStyle
        get() = sharedPreferencesHandler.animeListStyle ?: ListStyle()
        set(value) { sharedPreferencesHandler.animeListStyle = value }

    override var mangaListStyle: ListStyle
        get() = sharedPreferencesHandler.mangaListStyle ?: ListStyle()
        set(value) { sharedPreferencesHandler.mangaListStyle = value }

    override var animeFilter: MediaFilter
        get() = sharedPreferencesHandler.animeFilter ?: MediaFilter()
        set(value) { sharedPreferencesHandler.animeFilter = value }

    override var mangaFilter: MediaFilter
        get() = sharedPreferencesHandler.mangaFilter ?: MediaFilter()
        set(value) { sharedPreferencesHandler.mangaFilter = value }

    override var appSetting: AppSetting
        get() = sharedPreferencesHandler.appSetting ?: AppSetting()
        set(value) { sharedPreferencesHandler.appSetting = value }

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
        get() = sharedPreferencesHandler.followingCount
        set(value) { sharedPreferencesHandler.followingCount = value }

    override var followersCount: Int?
        get() = sharedPreferencesHandler.followersCount
        set(value) { sharedPreferencesHandler.followersCount = value }

    override var animeList: SaveItem<MediaListCollection>?
        get() = jsonStorageHandler.animeList
        set(value) { jsonStorageHandler.animeList = value }

    override var mangaList: SaveItem<MediaListCollection>?
        get() = jsonStorageHandler.mangaList
        set(value) { jsonStorageHandler.mangaList = value }
}