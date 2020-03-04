package com.zen.alchan.ui.settings.app

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.helper.enums.AppColorTheme

class AppSettingsViewModel(private val appSettingsRepository: AppSettingsRepository) : ViewModel() {

    var isInit = false
    var selectedAppTheme: AppColorTheme? = null

    val appColorTheme: AppColorTheme
        get() = appSettingsRepository.appColorTheme

    val homeShowWatching: Boolean
        get() = appSettingsRepository.homeShowWatching

    val homeShowReading: Boolean
        get() = appSettingsRepository.homeShowReading

    fun setAppSettings(appColorTheme: AppColorTheme, homeShowWatching: Boolean, homeShowReading: Boolean) {
        appSettingsRepository.setAppSettings(appColorTheme, homeShowWatching, homeShowReading)
    }
}