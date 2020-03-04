package com.zen.alchan.ui.base

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.UserRepository

class BaseViewModel(private val appSettingsRepository: AppSettingsRepository) : ViewModel() {

    val appColorThemeResource: Int
        get() = appSettingsRepository.appColorThemeResource
}