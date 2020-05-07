package com.zen.alchan.data.localstorage

import androidx.lifecycle.LiveData
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.PushNotificationsSettings
import type.StaffLanguage

interface AppSettingsManager {
    val appColorTheme: AppColorTheme
    val voiceActorLanguage: StaffLanguage

    val pushNotificationsSettings: PushNotificationsSettings

    fun setAppColorTheme(appColorTheme: AppColorTheme)

    fun setVoiceActorLanguage(staffLanguage: StaffLanguage)

    fun setPushNotificationsSettings(pushNotificationsSettings: PushNotificationsSettings)
}