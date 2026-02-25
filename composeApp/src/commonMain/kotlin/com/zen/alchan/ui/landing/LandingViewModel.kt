package com.zen.alchan.ui.landing

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.ConfigRepository
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LandingViewModel(
    dispatcher: Dispatcher,
    private val configRepository: ConfigRepository,
    private val authRepository: AuthRepository
) : BaseViewModel<LandingUiState, LandingUiEffect>(
    LandingUiState(), dispatcher,
) {
    init {
        loadData()
    }

    private fun loadData() {
//        viewModelScope.launch(dispatcher.io) {
//            delay(1000)
//            sendNewEffect(LandingUiEffect.NavigateToLogin)
//        }
    }
}

data class LandingUiState(
    val isLoading: Boolean = false
)

sealed interface LandingUiEffect {
    object NavigateToLogin : LandingUiEffect
    object NavigateToMain : LandingUiEffect
}
