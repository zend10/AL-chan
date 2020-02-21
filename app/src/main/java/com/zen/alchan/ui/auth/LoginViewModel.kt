package com.zen.alchan.ui.auth

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AuthRepository

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val viewerDataResponse by lazy {
        authRepository.viewerDataResponse
    }

    fun doLogin(accessToken: String) {
        authRepository.setBearerToken(accessToken)
        authRepository.retrieveViewerData()
    }
}