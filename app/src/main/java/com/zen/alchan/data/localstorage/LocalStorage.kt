package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.pojo.PushNotificationsSettings
import type.StaffLanguage

interface LocalStorage {
    var bearerToken: String?

    var appColorTheme: AppColorTheme
    var voiceActorLanguage: StaffLanguage

    var pushNotificationsSettings: PushNotificationsSettings

    var viewerData: User?
    var viewerDataLastRetrieved: Long?

    var genreList: List<String>?
    var genreListLastRetrieved: Long?

    var animeListStyle: ListStyle?
    var mangaListStyle: ListStyle?
}