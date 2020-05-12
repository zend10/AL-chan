package com.zen.alchan.ui.main

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository

class MainViewModel(private val appSettingsRepository: AppSettingsRepository,
                    private val userRepository: UserRepository,
                    private val mediaListRepository: MediaListRepository
) : ViewModel() {

    val appColorThemeLiveData by lazy {
        appSettingsRepository.appColorThemeLiveData
    }

    val listOrAniListSettingsChanged by lazy {
        userRepository.listOrAniListSettingsChanged
    }

    val shouldLoading by lazy {
        mediaListRepository.shouldLoading
    }
}