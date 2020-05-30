package com.zen.alchan.ui.auth

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.InfoRepository
import com.zen.alchan.helper.enums.AppColorTheme

class SplashViewModel(private val authRepository: AuthRepository,
                      private val infoRepository: InfoRepository) : ViewModel() {

    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn

    val lastAnnouncementId: Int?
        get() = infoRepository.lastAnnouncementId

    val announcementResponse by lazy {
        infoRepository.announcementResponse
    }

    fun getAnnouncement() {
        infoRepository.getAnnouncement()
    }

    fun setNeverShowAgain(id: Int) {
        infoRepository.setLastAnnouncementId(id)
    }
}