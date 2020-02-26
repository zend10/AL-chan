package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.PushNotificationsSettings

interface LocalStorage {
    var bearerToken: String?

    var appColorTheme: AppColorTheme
    var homeShowWatching: Boolean
    var homeShowReading: Boolean

    var pushNotificationsSettings: PushNotificationsSettings

    var viewerData: User?
    var viewerDataLastRetrieved: Long?
}