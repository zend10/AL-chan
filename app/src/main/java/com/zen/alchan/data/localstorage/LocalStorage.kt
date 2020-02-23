package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.AppColorTheme

interface LocalStorage {
    var bearerToken: String?

    var appColorTheme: AppColorTheme
    var homeShowWatching: Boolean
    var homeShowReading: Boolean

    var pushNotifAiring: Boolean
    var pushNotifActivity: Boolean
    var pushNotifForum: Boolean
    var pushNotifFollows: Boolean

    var viewerData: User?
    var viewerDataLastRetrieved: Long?
}