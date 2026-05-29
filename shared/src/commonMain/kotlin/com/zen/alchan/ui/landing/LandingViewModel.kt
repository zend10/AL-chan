package com.zen.alchan.ui.landing

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.repository.ConfigRepository
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.coroutines.launch

class LandingViewModel(
    dispatcher: Dispatcher,
    private val configRepository: ConfigRepository
) : BaseViewModel<LandingUiState, LandingUiEffect>(
    LandingUiState(), dispatcher,
) {
    init {
        loadData()
    }

    private fun loadData() {

    }

    fun onStartClick() {
        viewModelScope.launch(dispatcher.io) {
            configRepository.setLandingCompleted()
            sendNewEffect(LandingUiEffect.NavigateToMain)
        }
    }
}

data class LandingUiState(
    val isLoading: Boolean = false,
)

sealed interface LandingUiEffect {
    object NavigateToMain : LandingUiEffect
}