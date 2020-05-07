package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.PushNotificationsSettings
import type.StaffLanguage

interface AppSettingsRepository {
    val appColorThemeResource: Int
    val appColorThemeLiveData: LiveData<Int>

    val appColorTheme: AppColorTheme
    val voiceActorLanguage: StaffLanguage
    val pushNotificationsSettings: PushNotificationsSettings

    fun setAppSettings(appColorTheme: AppColorTheme, voiceActorLanguage: StaffLanguage)
    fun setPushNotificationsSettings(pushNotifAiring: Boolean, pushNotifActivity: Boolean, pushNotifForum: Boolean, pushNotifFollows: Boolean)
}