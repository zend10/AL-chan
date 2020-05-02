package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.PushNotificationsSettings
import type.StaffLanguage

interface AppSettingsRepository {
    val appColorThemeResource: Int
    val appColorThemeLiveData: LiveData<Int>

    val appColorTheme: AppColorTheme
    val homeShowWatching: Boolean
    val homeShowReading: Boolean
    val voiceActorLanguage: StaffLanguage
    val pushNotificationsSettings: PushNotificationsSettings

    // TODO: change this later
    fun setAppSettings(appColorTheme: AppColorTheme, homeShowWatching: Boolean, homeShowReading: Boolean, voiceActorLanguage: StaffLanguage)
    fun setPushNotificationsSettings(pushNotifAiring: Boolean, pushNotifActivity: Boolean, pushNotifForum: Boolean, pushNotifFollows: Boolean)
}