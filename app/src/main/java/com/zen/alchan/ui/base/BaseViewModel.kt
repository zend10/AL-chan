package com.zen.alchan.ui.base

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AuthRepository

class BaseViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val appColorTheme: Int
        get() = authRepository.appColorTheme
}