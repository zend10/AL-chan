package com.zen.alchan.ui.home

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.News
import com.zen.alchan.data.model.api.Media
import com.zen.alchan.data.model.api.User
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.ConfigRepository
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.helper.AniListConstant
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    dispatcher: Dispatcher,
    private val contentRepository: ContentRepository,
    private val configRepository: ConfigRepository,
    private val authRepository: AuthRepository,
    private val aniListConstant: AniListConstant
) : BaseViewModel<HomeUiState, HomeUiEffect>(
    HomeUiState(), dispatcher
) {
    init {
        viewModelScope.launch(dispatcher.io) {
            authRepository.loginTrigger.collectLatest { token ->
                login(token)
            }
        }

        loadData()
    }

    private fun login(token: String) {
        viewModelScope.launch(dispatcher.io) {
            try {
                updateState { it.copy(isLoggingIn = true) }
                val user = authRepository.login(token)
                updateState { it.copy(isLoggingIn = false, user = user) }
            } catch (exception: Exception) {

            }
        }
    }

    private fun loadData() {
        viewModelScope.launch(dispatcher.io) {
            try {
                val appConfig = configRepository.getAppConfig()
                val user = authRepository.getLocalCurrentUser()
                updateState {
                    it.copy(
                        appConfig = appConfig,
                        user = user
                    )
                }
                loadHomeData()
            } catch (exception: Exception) {

            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch(dispatcher.io) {
            try {
                updateState { it.copy(isLoading = true) }
                val homeData = contentRepository.getHomeData()
                updateState {
                    it.copy(
                        isLoading = false,
                        news = homeData.getNews()
                    )
                }
            } catch (exception: Exception) {

            }
        }
    }

    fun onRegisterPressed() {
        sendNewEffect(HomeUiEffect.NavigateToWeb(aniListConstant.ANILIST_REGISTER_URL))
    }

    fun onLoginPressed() {
        sendNewEffect(HomeUiEffect.NavigateToWeb(aniListConstant.ANILIST_LOGIN_URL))
    }

    fun onProfilePressed() {
        sendNewEffect(HomeUiEffect.NavigateToProfile)
    }

    fun onNotificationsPressed() {
        sendNewEffect(HomeUiEffect.NavigateToNotifications)
    }

    fun onSearchPressed() {
        sendNewEffect(HomeUiEffect.NavigateToSearch)
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
    val isLoggingIn: Boolean = false,
    val isLoading: Boolean = false,
    val appConfig: AppConfig = AppConfig(),
    val user: User = User(),
    val news: List<News> = listOf()
)

sealed interface HomeUiEffect {
    object NavigateToSeasonal : HomeUiEffect
    object NavigateToExplore : HomeUiEffect
    object NavigateToCalendar : HomeUiEffect
    object NavigateToSocial : HomeUiEffect
    class NavigateToMediaDetail(val media: Media) : HomeUiEffect
    class NavigateToWeb(val url: String) : HomeUiEffect
    object NavigateToProfile : HomeUiEffect
    object NavigateToNotifications : HomeUiEffect
    object NavigateToSearch : HomeUiEffect
}