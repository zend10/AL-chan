package com.zen.alchan.data.localstorage

import androidx.lifecycle.LiveData
import com.zen.alchan.helper.enums.AppColorTheme

interface AppSettingsManager {
    val appColorTheme: AppColorTheme
    val appColorThemeLiveData: LiveData<AppColorTheme>

    val homeShowWatching: Boolean
    val homeShowWatchingLiveData: LiveData<Boolean>
    val homeShowReading: Boolean
    val homeShowReadingLiveData: LiveData<Boolean>

    val pushNotifAiring: Boolean
    val pushNotifActivity: Boolean
    val pushNotifForum: Boolean
    val pushNotifFollows: Boolean

    fun setAppColorTheme(appColorTheme: AppColorTheme)

    fun setHomeShowWatching(value: Boolean)
    fun setHomeShowReading(value: Boolean)

    fun setPushNotifAiring(value: Boolean)
    fun setPushNotifActivity(value: Boolean)
    fun setPushNotifForum(value: Boolean)
    fun setPushNotifFollows(value: Boolean)
}