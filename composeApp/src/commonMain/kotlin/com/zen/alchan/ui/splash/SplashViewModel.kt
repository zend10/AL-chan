package com.zen.alchan.ui.splash

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.ConfigRepository
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.coroutines.launch

class SplashViewModel(
    dispatcher: Dispatcher,
    private val configRepository: ConfigRepository,
    private val authRepository: AuthRepository
) : BaseViewModel<SplashUiState, SplashUiEffect>(
    SplashUiState(), dispatcher,
) {
    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(dispatcher.io) {
            val appConfig = configRepository.getAppConfig()
            val isLoggedIn = authRepository.isLoggedIn()

            sendNewEffect(
                if (isLoggedIn) SplashUiEffect.NavigateToMain else SplashUiEffect.NavigateToLogin
            )
        }
    }
}

data class SplashUiState(
    val isLoading: Boolean = false
)

sealed interface SplashUiEffect {
    object NavigateToLogin : SplashUiEffect
    object NavigateToMain : SplashUiEffect
}
