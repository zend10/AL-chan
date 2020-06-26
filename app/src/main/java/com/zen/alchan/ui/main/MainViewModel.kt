package com.zen.alchan.ui.main

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository

class MainViewModel(private val appSettingsRepository: AppSettingsRepository,
                    private val userRepository: UserRepository
) : ViewModel() {

    val appColorThemeLiveData by lazy {
        appSettingsRepository.appColorThemeLiveData
    }

    val listOrAniListSettingsChanged by lazy {
        userRepository.listOrAniListSettingsChanged
    }

    val sessionResponse by lazy {
        userRepository.sessionResponse
    }

    val notificationCount by lazy {
        userRepository.notificationCount
    }

    fun checkSession() {
        userRepository.checkSession()
    }

    fun getNotificationCount() {
        userRepository.getNotificationCount()
    }

    fun clearStorage() {
        appSettingsRepository.clearStorage()
    }
}