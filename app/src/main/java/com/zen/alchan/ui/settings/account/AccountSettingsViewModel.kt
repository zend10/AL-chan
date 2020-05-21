package com.zen.alchan.ui.settings.account

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository

class AccountSettingsViewModel(private val appSettingsRepository: AppSettingsRepository) : ViewModel() {

    fun clearStorage() {
        appSettingsRepository.clearStorage()
    }
}