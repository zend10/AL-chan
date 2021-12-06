package com.zen.alchan.data.localstorage

import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.entity.ListStyle

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
}