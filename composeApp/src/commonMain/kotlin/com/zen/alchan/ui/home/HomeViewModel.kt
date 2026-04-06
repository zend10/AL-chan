package com.zen.alchan.ui.home

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.model.User
import com.zen.alchan.data.model.api.HomeData
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
                updateState { it.copy(isLoading = true) }
                val homeData = contentRepository.getHomeData()
                updateState {
                    it.copy(
                        isLoading = false,
                        homeData = homeData
                    )
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    fun onSeasonalPressed() {
        sendNewEffect(HomeUiEffect.NavigateToSeasonal)
    }

    fun onExplorePressed() {
        sendNewEffect(HomeUiEffect.NavigateToExplore)
    }

    fun onCalendarPressed() {
        sendNewEffect(HomeUiEffect.NavigateToCalendar)
    }

    fun onSocialPressed() {
        sendNewEffect(HomeUiEffect.NavigateToSocial)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User = User(),
    val homeData: HomeData = HomeData()
)

sealed interface HomeUiEffect {
    object NavigateToSeasonal : HomeUiEffect
    object NavigateToExplore : HomeUiEffect
    object NavigateToCalendar : HomeUiEffect
    object NavigateToSocial : HomeUiEffect
}