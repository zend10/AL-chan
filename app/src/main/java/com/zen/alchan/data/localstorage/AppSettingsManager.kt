package com.zen.alchan.data.localstorage

import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.AppSettings
import com.zen.alchan.helper.pojo.UserPreferences
import type.StaffLanguage

interface AppSettingsManager {
    val appSettings: AppSettings
    val userPreferences: UserPreferences

    fun setAppSettings(value: AppSettings)
    fun setUserPreferences(value: UserPreferences)

    fun clearStorage()
}