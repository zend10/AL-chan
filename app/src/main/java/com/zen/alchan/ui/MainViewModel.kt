package com.zen.alchan.ui

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AuthRepository

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val appColorThemeLiveData by lazy {
        authRepository.appColorThemeLiveData
    }
}