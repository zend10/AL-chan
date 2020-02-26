package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.PushNotificationsSettings
import type.ScoreFormat
import type.UserTitleLanguage

interface ProfileRepository {
    val viewerDataResponse: LiveData<Resource<Boolean>>
    val viewerData: LiveData<User?>

    val updateAniListSettingsResponse: LiveData<Resource<Boolean>>
    val updateListSettingsResponse: LiveData<Resource<Boolean>>

    val appColorTheme: AppColorTheme
    val homeShowWatching: Boolean
    val homeShowReading: Boolean
    val pushNotificationsSettings: PushNotificationsSettings

    fun getViewerData()
    fun retrieveViewerData()

    fun updateAniListSettings(titleLanguage: UserTitleLanguage, adultContent: Boolean, airingNotifications: Boolean)
    fun updateListSettings(scoreFormat: ScoreFormat, rowOrder: String, animeListOptions: MediaListTypeOptions, mangaListOptions: MediaListTypeOptions)

    fun setAppSettings(appColorTheme: AppColorTheme, homeShowWatching: Boolean, homeShowReading: Boolean)
    fun setPushNotificationsSettings(pushNotifAiring: Boolean, pushNotifActivity: Boolean, pushNotifForum: Boolean, pushNotifFollows: Boolean)
}