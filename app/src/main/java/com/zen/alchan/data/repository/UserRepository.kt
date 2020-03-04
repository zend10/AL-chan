package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.data.response.User
import type.ScoreFormat
import type.UserTitleLanguage

interface UserRepository {
    val viewerDataResponse: LiveData<Resource<Boolean>>
    val viewerData: LiveData<User?>
    val listOrAniListSettingsChanged: LiveData<Boolean>

    val updateAniListSettingsResponse: LiveData<Resource<Boolean>>
    val updateListSettingsResponse: LiveData<Resource<Boolean>>

    val viewerDataLastRetrieved: Long?

    fun getViewerData()
    fun retrieveViewerData()

    fun updateAniListSettings(titleLanguage: UserTitleLanguage, adultContent: Boolean, airingNotifications: Boolean)
    fun updateListSettings(scoreFormat: ScoreFormat, rowOrder: String, animeListOptions: MediaListTypeOptions, mangaListOptions: MediaListTypeOptions)
}