package com.zen.alchan.data.manager

import android.net.Uri
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.entitiy.MediaFilter
import com.zen.alchan.data.response.ProfileData
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.pojo.SaveItem
import io.reactivex.Observable
import java.io.File

interface UserManager {
    var bearerToken: String?
    val isAuthenticated: Boolean
    var isLoggedInAsGuest: Boolean

    var animeListStyle: ListStyle
    var mangaListStyle: ListStyle
    var animeFilter: MediaFilter
    var mangaFilter: MediaFilter
    var appSetting: AppSetting

    val animeListBackground: Observable<NullableItem<Uri>>
    val mangaListBackground: Observable<NullableItem<Uri>>
    fun saveAnimeListBackground(uri: Uri?): Observable<Unit>
    fun saveMangaListBackground(uri: Uri?): Observable<Unit>

    var viewerData: SaveItem<User>?
    var profileData: SaveItem<ProfileData>?

    var animeList: SaveItem<MediaListCollection>?
    var mangaList: SaveItem<MediaListCollection>?
}