package com.zen.alchan.ui.profile.settings.app

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.ProfileRepository
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.AppColorTheme
import kotlinx.coroutines.processNextEventInCurrentThread

class AppSettingsViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    var isInit = false
    var selectedAppTheme: AppColorTheme? = null

    val appColorTheme: AppColorTheme
        get() = profileRepository.appColorTheme

    val homeShowWatching: Boolean
        get() = profileRepository.homeShowWatching

    val homeShowReading: Boolean
        get() = profileRepository.homeShowReading

    fun setAppSettings(appColorTheme: AppColorTheme, homeShowWatching: Boolean, homeShowReading: Boolean) {
        profileRepository.setAppSettings(appColorTheme, homeShowWatching, homeShowReading)
    }
}