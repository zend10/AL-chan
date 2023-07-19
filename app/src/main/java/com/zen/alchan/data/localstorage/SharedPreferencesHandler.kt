package com.zen.alchan.data.localstorage

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.response.SpotifyAccessToken
import com.zen.alchan.helper.enums.ListType

interface SharedPreferencesHandler {
    var bearerToken: String?
    var guestLogin: Boolean?
    var animeListStyle: ListStyle?
    var mangaListStyle: ListStyle?
    var animeFilter: MediaFilter?
    var mangaFilter: MediaFilter?
    var appSetting: AppSetting?
    var followingCount: Int?
    var followersCount: Int?
    var othersListType: ListType?
    var lastNotificationId: Int?
    var lastAnnouncementId: String?
    var spotifyAccessToken: SpotifyAccessToken?
    var spotifyAccessTokenLastRetrieve: Long?
}