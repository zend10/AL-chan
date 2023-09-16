package com.zen.alchan.data.manager

import android.net.Uri
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.CalendarSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.response.anilist.MediaListCollection
import com.zen.alchan.helper.pojo.NullableItem
import com.zen.alchan.helper.pojo.SaveItem
import io.reactivex.rxjava3.core.Observable

interface UserManager {
    var bearerToken: String?
    val isAuthenticated: Boolean
    var isLoggedInAsGuest: Boolean

    var animeListStyle: ListStyle
    var mangaListStyle: ListStyle
    var animeFilter: MediaFilter
    var mangaFilter: MediaFilter
    var appSetting: AppSetting
    var calendarSetting: CalendarSetting

    val animeListBackground: Observable<NullableItem<Uri>>
    val mangaListBackground: Observable<NullableItem<Uri>>
    fun saveAnimeListBackground(uri: Uri?): Observable<Unit>
    fun saveMangaListBackground(uri: Uri?): Observable<Unit>

    var viewerData: SaveItem<User>?
    var followingCount: Int?
    var followersCount: Int?
    var animeListEntryCount: Int?
    var mangaListEntryCount: Int?

    var animeList: SaveItem<MediaListCollection>?
    var mangaList: SaveItem<MediaListCollection>?

    var lastNotificationId: Int?

    var lastAnnouncementId: String?
}