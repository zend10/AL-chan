package com.zen.alchan.ui.settings.app

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.helper.enums.AppColorTheme
import type.StaffLanguage

class AppSettingsViewModel(private val appSettingsRepository: AppSettingsRepository) : ViewModel() {

    var isInit = false
    var selectedAppTheme: AppColorTheme? = null
    var selectedLanguage: StaffLanguage? = null

    val appColorTheme: AppColorTheme
        get() = appSettingsRepository.appColorTheme

    val voiceActorLanguage: StaffLanguage
        get() = appSettingsRepository.voiceActorLanguage

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

    fun setAppSettings(appColorTheme: AppColorTheme, voiceActorLanguage: StaffLanguage) {
        appSettingsRepository.setAppSettings(appColorTheme, voiceActorLanguage)
    }
}