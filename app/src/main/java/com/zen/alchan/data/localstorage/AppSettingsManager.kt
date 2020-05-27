package com.zen.alchan.data.localstorage

import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.AppSettings
import type.StaffLanguage

interface AppSettingsManager {
    val appSettings: AppSettings

    fun setAppSettings(value: AppSettings)

    fun clearStorage()
}