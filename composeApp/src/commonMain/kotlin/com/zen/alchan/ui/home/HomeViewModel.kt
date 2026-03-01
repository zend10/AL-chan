package com.zen.alchan.ui.home

import com.zen.alchan.data.model.User
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher

class HomeViewModel(
    dispatcher: Dispatcher,
) : BaseViewModel<HomeUiState, HomeUiEffect>(
    HomeUiState(), dispatcher
) {
    init {
        loadData()
    }

    private fun loadData() {

    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User = User()
) {
}

sealed interface HomeUiEffect {
    object NavigateToSocial : HomeUiEffect

}