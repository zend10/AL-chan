package com.zen.alchan.ui.landing

import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher

class LandingViewModel(
    dispatcher: Dispatcher,
    private val authRepository: AuthRepository
) : BaseViewModel<LandingUiState, LandingUiEffect>(
    LandingUiState(), dispatcher,
) {
    init {
        loadData()
    }

    fun loadData() {

    }

    fun onStartPressed() {
        sendNewEffect(LandingUiEffect.NavigateToMain)
    }
}

data class LandingUiState(
    val isLoading: Boolean = false,
)

sealed interface LandingUiEffect {
    object NavigateToMain : LandingUiEffect
}