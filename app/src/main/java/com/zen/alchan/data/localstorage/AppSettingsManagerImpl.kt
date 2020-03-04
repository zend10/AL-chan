package com.zen.alchan.data.localstorage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.PushNotificationsSettings

class AppSettingsManagerImpl(private val localStorage: LocalStorage) : AppSettingsManager {

    override val appColorTheme: AppColorTheme
        get() = localStorage.appColorTheme

    override val homeShowWatching: Boolean
        get() = localStorage.homeShowWatching

    override val homeShowReading: Boolean
        get() = localStorage.homeShowReading

    override val pushNotificationsSettings: PushNotificationsSettings
        get() = localStorage.pushNotificationsSettings

    override fun setAppColorTheme(appColorTheme: AppColorTheme) {
        localStorage.appColorTheme = appColorTheme
    }

    override fun setHomeShowWatching(value: Boolean) {
        localStorage.homeShowWatching = value
    }

    override fun setHomeShowReading(value: Boolean) {
        localStorage.homeShowReading = value
    }

    override fun setPushNotificationsSettings(pushNotificationsSettings: PushNotificationsSettings) {
        localStorage.pushNotificationsSettings = pushNotificationsSettings
    }
}