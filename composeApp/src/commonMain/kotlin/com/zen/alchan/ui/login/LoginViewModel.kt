package com.zen.alchan.ui.login

import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher

class LoginViewModel(dispatcher: Dispatcher) : BaseViewModel<LoginUiState, LoginUiEffect>(
    LoginUiState(),
    dispatcher,
) {
    init {
        loadData()
    }

    fun loadData() {

    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
)

sealed interface LoginUiEffect {
    object NavigateToMain : LoginUiEffect
}