package com.zen.alchan.data.localstorage

import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.AppColorTheme

interface LocalStorage {
    var appColorTheme: AppColorTheme
    var bearerToken: String?

    var viewerData: User?
    var viewerDataLastRetrieved: Long?
}