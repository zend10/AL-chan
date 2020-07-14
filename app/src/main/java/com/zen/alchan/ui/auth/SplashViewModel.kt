package com.zen.alchan.ui.auth

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.InfoRepository
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.pojo.AppSettings

class SplashViewModel(private val authRepository: AuthRepository,
                      private val infoRepository: InfoRepository,
                      private val appSettingsRepository: AppSettingsRepository) : ViewModel() {

    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn

    val lastAnnouncementId: Int?
        get() = infoRepository.lastAnnouncementId

    val appSettings: AppSettings
        get() = appSettingsRepository.appSettings

    val announcementResponse by lazy {
        infoRepository.announcementResponse
    }

    fun getAnnouncement() {
        infoRepository.getAnnouncement()
    }

    fun setNeverShowAgain(id: Int) {
        infoRepository.setLastAnnouncementId(id)
    }

    fun setDefaultAppSetting(isLowOnMemory: Boolean) {
        appSettingsRepository.setDefaultSetting(isLowOnMemory)
    }
}