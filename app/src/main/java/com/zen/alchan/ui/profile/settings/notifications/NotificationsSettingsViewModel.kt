package com.zen.alchan.ui.profile.settings.notifications

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.ProfileRepository
import com.zen.alchan.helper.pojo.PushNotificationsSettings

class NotificationsSettingsViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    var isInit = false

    val pushNotificationsSettings: PushNotificationsSettings
        get() = profileRepository.pushNotificationsSettings
}