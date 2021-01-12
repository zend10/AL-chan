package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.MediaTagCollection
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.AppSettings
import com.zen.alchan.helper.pojo.BestFriend
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.pojo.UserPreferences
import type.StaffLanguage

interface LocalStorage {
    var bearerToken: String?

    var appSettings: AppSettings
    var userPreferences: UserPreferences

    var viewerData: User?
    var viewerDataLastRetrieved: Long?

    var followersCount: Int?
    var followersCountLastRetrieved: Long?

    var followingsCount: Int?
    var followingsCountLastRetrieved: Long?

    var genreList: List<String?>?
    var genreListLastRetrieved: Long?

    var tagList: List<MediaTagCollection>?
    var tagListLastRetrieved: Long?

    var mostTrendingAnimeBanner: String?

    var animeListStyle: ListStyle
    var mangaListStyle: ListStyle

    var lastAnnouncementId: Int?

    var bestFriends: List<BestFriend>?

    var latestNotification: Int?

    var lastPushNotificationTimestamp: Long?

    fun clearStorage()
}