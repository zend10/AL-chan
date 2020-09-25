package com.zen.alchan.data.localstorage

import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.AppSettings
import type.StaffLanguage

class AppSettingsManagerImpl(private val localStorage: LocalStorage) : AppSettingsManager {

    override val appSettings: AppSettings
        get() {
            val savedSettings = localStorage.appSettings
            if (savedSettings.appTheme == null) savedSettings.appTheme = AppColorTheme.DEFAULT_THEME_YELLOW
            if (savedSettings.circularAvatar == null) savedSettings.circularAvatar = true
            if (savedSettings.whiteBackgroundAvatar == null) savedSettings.whiteBackgroundAvatar = true
            if (savedSettings.voiceActorLanguage == null) savedSettings.voiceActorLanguage = StaffLanguage.JAPANESE
            if (savedSettings.showRecentReviews == null) savedSettings.showRecentReviews = true
            if (savedSettings.useRelativeDate == null) savedSettings.useRelativeDate = false
            // add more to here when adding new settings
            return savedSettings
        }

    override fun setAppSettings(value: AppSettings) {
        localStorage.appSettings = value
    }

    override fun clearStorage() {
        localStorage.clearStorage()
    }
}