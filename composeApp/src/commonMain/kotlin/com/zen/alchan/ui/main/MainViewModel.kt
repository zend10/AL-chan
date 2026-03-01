package com.zen.alchan.ui.main

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zen.alchan.data.repository.ConfigRepository
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.coroutines.launch

class MainViewModel(
    dispatcher: Dispatcher,
    private val configRepository: ConfigRepository
) : BaseViewModel<MainUiState, MainUiEffect>(
    MainUiState(), dispatcher
) {

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(dispatcher.io) {
            val appConfig = configRepository.getAppConfig()
            updateState { it.copy(defaultTabIndex = appConfig.defaultMainTabIndex) }
        }
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val defaultTabIndex: Int = 0,
)

sealed interface MainUiEffect {

}