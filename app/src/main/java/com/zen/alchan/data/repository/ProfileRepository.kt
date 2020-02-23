package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.AppColorTheme

interface ProfileRepository {
    val viewerDataResponse: LiveData<Resource<Boolean>>
    val viewerData: LiveData<User?>

    val appColorTheme: AppColorTheme
    val homeShowWatching: Boolean
    val homeShowReading: Boolean
    val pushNotifAiring: Boolean
    val pushNotifActivity: Boolean
    val pushNotifForum: Boolean
    val pushNotifFollows: Boolean

    fun getViewerData()
    fun retrieveViewerData()

    fun setAppSettings(
        appColorTheme: AppColorTheme,
        homeShowWatching: Boolean,
        homeShowReading: Boolean,
        pushNotifAiring: Boolean,
        pushNotifActivity: Boolean,
        pushNotifForum: Boolean,
        pushNotifFollows: Boolean
    )
}