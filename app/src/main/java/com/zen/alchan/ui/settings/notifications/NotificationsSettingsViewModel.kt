package com.zen.alchan.ui.settings.notifications

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.helper.pojo.PushNotificationsSettings

class NotificationsSettingsViewModel(private val appSettingsRepository: AppSettingsRepository) : ViewModel() {

    var isInit = false

    val pushNotificationsSettings: PushNotificationsSettings
        get() = appSettingsRepository.pushNotificationsSettings
}