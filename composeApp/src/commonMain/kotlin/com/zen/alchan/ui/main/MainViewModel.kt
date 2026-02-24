package com.zen.alchan.ui.main

import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher

class MainViewModel(dispatcher: Dispatcher) : BaseViewModel<MainUiState, MainUiEffect>(
    MainUiState(),
    dispatcher,
) {

    init {
        loadData()
    }

    fun loadData() {

    }
}

data class MainUiState(
    val isLoading: Boolean = false,
)

sealed interface MainUiEffect {

}