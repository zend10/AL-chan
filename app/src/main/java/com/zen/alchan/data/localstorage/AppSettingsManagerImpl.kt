package com.zen.alchan.data.localstorage

import com.zen.alchan.helper.enums.AppColorTheme
import type.StaffLanguage

class AppSettingsManagerImpl(private val localStorage: LocalStorage) : AppSettingsManager {

    override val appColorTheme: AppColorTheme
        get() = localStorage.appColorTheme

    override val voiceActorLanguage: StaffLanguage
        get() = localStorage.voiceActorLanguage

    override fun setAppColorTheme(appColorTheme: AppColorTheme) {
        localStorage.appColorTheme = appColorTheme
    }

    override fun setVoiceActorLanguage(staffLanguage: StaffLanguage) {
        localStorage.voiceActorLanguage = staffLanguage
    }

    override fun clearStorage() {
        localStorage.clearStorage()
    }
}