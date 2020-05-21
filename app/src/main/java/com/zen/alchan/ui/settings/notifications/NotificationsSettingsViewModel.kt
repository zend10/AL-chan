package com.zen.alchan.ui.settings.notifications

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.NotificationOption

class NotificationsSettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

    var isInit = false

    val viewerData by lazy {
        userRepository.viewerData
    }

    val updateAniListSettingsResponse by lazy {
        userRepository.updateAniListSettingsResponse
    }

    fun initData() {
        userRepository.getViewerData()
    }

    fun updateNotificationsSettings(notificationOptions: List<NotificationOption>) {
        if (notificationOptions.isNullOrEmpty()) {
            return
        }
        userRepository.updateNotificationsSettings(notificationOptions)
    }
}