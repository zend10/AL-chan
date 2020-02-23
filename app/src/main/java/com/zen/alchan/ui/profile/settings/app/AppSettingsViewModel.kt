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

    val pushNotifAiring: Boolean
        get() = profileRepository.pushNotifAiring

    val pushNotifActivity: Boolean
        get() = profileRepository.pushNotifActivity

    val pushNotifForum: Boolean
        get() = profileRepository.pushNotifForum

    val pushNotifFollows: Boolean
        get() = profileRepository.pushNotifFollows

    fun setAppSettings(
        appColorTheme: AppColorTheme,
        homeShowWatching: Boolean,
        homeShowReading: Boolean,
        pushNotifAiring: Boolean,
        pushNotifActivity: Boolean,
        pushNotifForum: Boolean,
        pushNotifFollows: Boolean
    ) {
        profileRepository.setAppSettings(
            appColorTheme, homeShowWatching, homeShowReading, pushNotifAiring, pushNotifActivity, pushNotifForum, pushNotifFollows
        )
    }
}