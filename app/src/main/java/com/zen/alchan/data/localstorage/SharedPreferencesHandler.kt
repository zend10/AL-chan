package com.zen.alchan.data.localstorage

import com.zen.alchan.data.entitiy.AppSetting

interface SharedPreferencesHandler {
    var bearerToken: String?
    var guestLogin: Boolean?

    var appSetting: AppSetting?
}