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
) : BaseViewModel<Unit, SplashUiEffect>(
    Unit, dispatcher,
) {
    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(dispatcher.io) {
            var landingCompleted = false
            try {
                val remoteConfig = configRepository.getRemoteConfig()
                landingCompleted = configRepository.getLandingCompleted()
                if (landingCompleted) {
                    authRepository.getCurrentUser()
                }
            } catch (exception: Exception) {

            } finally {
                sendNewEffect(
                    if (landingCompleted) SplashUiEffect.NavigateToMain else SplashUiEffect.NavigateToLanding
                )
            }
        }
    }
}

sealed interface SplashUiEffect {
    object NavigateToLanding : SplashUiEffect
    object NavigateToMain : SplashUiEffect
}
