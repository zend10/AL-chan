package com.zen.alchan.ui.base

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.AppColorTheme

// view model for BaseActivity
class BaseViewModel(private val appSettingsRepository: AppSettingsRepository) : ViewModel() {

    val appColorThemeResource: Int
        get() = appSettingsRepository.appColorThemeResource

    val appColorTheme: AppColorTheme?
        get() = appSettingsRepository.appSettings.appTheme
}