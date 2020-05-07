package com.zen.alchan.data.localstorage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.PushNotificationsSettings
import type.StaffLanguage

class AppSettingsManagerImpl(private val localStorage: LocalStorage) : AppSettingsManager {

    override val appColorTheme: AppColorTheme
        get() = localStorage.appColorTheme

    override val voiceActorLanguage: StaffLanguage
        get() = localStorage.voiceActorLanguage

    override val pushNotificationsSettings: PushNotificationsSettings
        get() = localStorage.pushNotificationsSettings

    override fun setAppColorTheme(appColorTheme: AppColorTheme) {
        localStorage.appColorTheme = appColorTheme
    }

    override fun setVoiceActorLanguage(staffLanguage: StaffLanguage) {
        localStorage.voiceActorLanguage = staffLanguage
    }

    override fun setPushNotificationsSettings(pushNotificationsSettings: PushNotificationsSettings) {
        localStorage.pushNotificationsSettings = pushNotificationsSettings
    }
}