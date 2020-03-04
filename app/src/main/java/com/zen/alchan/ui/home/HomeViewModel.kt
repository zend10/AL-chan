package com.zen.alchan.ui.home

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.utils.Utility

class HomeViewModel(private val userRepository: UserRepository,
                    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    val homeShowWatching: Boolean
        get() = appSettingsRepository.homeShowWatching

    val homeShowReading: Boolean
        get() = appSettingsRepository.homeShowReading

    val viewerDataResponse by lazy {
        userRepository.viewerDataResponse
    }

    val viewerData by lazy {
        userRepository.viewerData
    }

    fun initData() {
        userRepository.getViewerData()

        if (Utility.timeDiffMoreThanOneDay(userRepository.viewerDataLastRetrieved)) {
            userRepository.retrieveViewerData()
        }
    }
}