package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.PushNotificationsSettings

interface AppSettingsRepository {
    val appColorThemeResource: Int
    val appColorThemeLiveData: LiveData<Int>

    val appColorTheme: AppColorTheme
    val homeShowWatching: Boolean
    val homeShowReading: Boolean
    val pushNotificationsSettings: PushNotificationsSettings

    fun setAppSettings(appColorTheme: AppColorTheme, homeShowWatching: Boolean, homeShowReading: Boolean)
    fun setPushNotificationsSettings(pushNotifAiring: Boolean, pushNotifActivity: Boolean, pushNotifForum: Boolean, pushNotifFollows: Boolean)
}