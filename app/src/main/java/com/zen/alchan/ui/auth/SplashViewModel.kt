package com.zen.alchan.ui.auth

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.helper.enums.AppColorTheme

class SplashViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn
}