package com.zen.alchan.ui.home

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.enums.MediaType
import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.User
import com.zen.alchan.data.model.api.HomeData
import com.zen.alchan.data.model.api.Media
import com.zen.alchan.data.repository.ConfigRepository
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.coroutines.launch

class HomeViewModel(
    dispatcher: Dispatcher,
    private val contentRepository: ContentRepository,
    private val configRepository: ConfigRepository
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
                val appConfig = configRepository.getAppConfig()
                val homeData = contentRepository.getHomeData()
                updateState {
                    it.copy(
                        isLoading = false,
                        appConfig = appConfig,
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

    fun onMediaPressed(media: Media) {
        sendNewEffect(HomeUiEffect.NavigateToMediaDetail(media))
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val appConfig: AppConfig = AppConfig(),
    val user: User = User(),
    val homeData: HomeData = HomeData(),
    val trendingMediaType: MediaType = MediaType.ANIME
)

sealed interface HomeUiEffect {
    object NavigateToSeasonal : HomeUiEffect
    object NavigateToExplore : HomeUiEffect
    object NavigateToCalendar : HomeUiEffect
    object NavigateToSocial : HomeUiEffect
    class NavigateToMediaDetail(val media: Media) : HomeUiEffect
}