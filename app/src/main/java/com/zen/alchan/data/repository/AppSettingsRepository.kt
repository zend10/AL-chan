package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.helper.enums.AppColorTheme
import type.StaffLanguage

interface AppSettingsRepository {
    val appColorThemeResource: Int
    val appColorThemeLiveData: LiveData<Int>

    val appColorTheme: AppColorTheme
    val voiceActorLanguage: StaffLanguage

    fun setAppSettings(appColorTheme: AppColorTheme, voiceActorLanguage: StaffLanguage)
    fun clearStorage()
}