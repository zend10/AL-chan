package com.zen.alchan.ui.home

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.model.api.Media
import com.zen.alchan.data.model.User
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.coroutines.launch

class HomeViewModel(
    dispatcher: Dispatcher,
    private val contentRepository: ContentRepository,
) : BaseViewModel<HomeUiState, HomeUiEffect>(
    HomeUiState(), dispatcher
) {
    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(dispatcher.io) {
            try {
                val trending = contentRepository.getTrending()
                updateState { it.copy(trending = trending) }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User = User(),
    val trending: List<Media> = listOf()
) {
}

sealed interface HomeUiEffect {
    object NavigateToSocial : HomeUiEffect

}