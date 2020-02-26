package com.zen.alchan.data.localstorage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.PushNotificationsSettings

class AppSettingsManagerImpl(private val localStorage: LocalStorage) : AppSettingsManager {

    override val appColorTheme: AppColorTheme
        get() = localStorage.appColorTheme

    private val _appColorThemeLiveData = MutableLiveData<AppColorTheme>()
    override val appColorThemeLiveData: LiveData<AppColorTheme>
        get() = _appColorThemeLiveData

    override val homeShowWatching: Boolean
        get() = localStorage.homeShowWatching

    private val _homeShowWatchingLiveData = MutableLiveData<Boolean>()
    override val homeShowWatchingLiveData: LiveData<Boolean>
        get() = _homeShowWatchingLiveData

    override val homeShowReading: Boolean
        get() = localStorage.homeShowReading

    private val _homeShowReadingLiveData = MutableLiveData<Boolean>()
    override val homeShowReadingLiveData: LiveData<Boolean>
        get() = _homeShowReadingLiveData

    override val pushNotificationsSettings: PushNotificationsSettings
        get() = localStorage.pushNotificationsSettings

    override fun setAppColorTheme(appColorTheme: AppColorTheme) {
        localStorage.appColorTheme = appColorTheme
        _appColorThemeLiveData.postValue(appColorTheme)
    }

    override fun setHomeShowWatching(value: Boolean) {
        localStorage.homeShowWatching = value
        _homeShowWatchingLiveData.postValue(value)
    }

    override fun setHomeShowReading(value: Boolean) {
        localStorage.homeShowReading = value
        _homeShowReadingLiveData.postValue(value)
    }

    override fun setPushNotificationsSettings(pushNotificationsSettings: PushNotificationsSettings) {
        localStorage.pushNotificationsSettings = pushNotificationsSettings
    }
}