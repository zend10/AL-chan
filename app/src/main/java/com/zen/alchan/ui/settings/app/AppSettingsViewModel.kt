package com.zen.alchan.ui.settings.app

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.AppSettings
import type.StaffLanguage

class AppSettingsViewModel(private val appSettingsRepository: AppSettingsRepository) : ViewModel() {

    var isInit = false
    var selectedAppTheme: AppColorTheme? = null
    var selectedLanguage: StaffLanguage? = null

    val appSettings: AppSettings
        get() = appSettingsRepository.appSettings

    val staffLanguageArray = arrayOf(
        StaffLanguage.JAPANESE.name,
        StaffLanguage.ENGLISH.name,
        StaffLanguage.KOREAN.name,
        StaffLanguage.ITALIAN.name,
        StaffLanguage.SPANISH.name,
        StaffLanguage.PORTUGUESE.name,
        StaffLanguage.FRENCH.name,
        StaffLanguage.GERMAN.name,
        StaffLanguage.HEBREW.name,
        StaffLanguage.HUNGARIAN.name
    )

    fun setAppSettings(
        circularAvatar: Boolean = true,
        whiteBackgroundAvatar: Boolean = true,
        showRecentReviews: Boolean = true,
        showSocialTab: Boolean,
        showBio: Boolean,
        showStats: Boolean,
        useRelativeDate: Boolean = false
    ) {
        appSettingsRepository.setAppSettings(AppSettings(
            appTheme = selectedAppTheme,
            circularAvatar = circularAvatar,
            whiteBackgroundAvatar = whiteBackgroundAvatar,
            voiceActorLanguage = selectedLanguage,
            showRecentReviews = showRecentReviews,
            showSocialTabAutomatically = showSocialTab,
            showBioAutomatically = showBio,
            showStatsAutomatically = showStats,
            useRelativeDate = useRelativeDate
        ))
    }
}