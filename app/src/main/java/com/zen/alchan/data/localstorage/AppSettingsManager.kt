package com.zen.alchan.data.localstorage

import com.zen.alchan.helper.enums.AppColorTheme
import type.StaffLanguage

interface AppSettingsManager {
    val appColorTheme: AppColorTheme
    val voiceActorLanguage: StaffLanguage

    fun setAppColorTheme(appColorTheme: AppColorTheme)
    fun setVoiceActorLanguage(staffLanguage: StaffLanguage)

    fun clearStorage()
}