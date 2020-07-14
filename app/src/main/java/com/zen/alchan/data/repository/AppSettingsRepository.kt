package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.AppSettings
import type.StaffLanguage

interface AppSettingsRepository {
    val appColorThemeResource: Int
    val appColorThemeLiveData: LiveData<Int>

    val appSettings: AppSettings

    fun setAppSettings(appSettings: AppSettings)
    fun setDefaultSetting(isLowOnMemory: Boolean)
    fun clearStorage()
}