package com.zen.alchan.ui.main

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.enums.BottomNavigationTab
import com.zen.alchan.data.repository.ConfigRepository
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel(
    dispatcher: Dispatcher,
    private val configRepository: ConfigRepository
) : BaseViewModel<MainUiState, MainUiEffect>(
    MainUiState(), dispatcher
) {

    private val _effect = MutableSharedFlow<MainUiEffect>()
    val bottomNavigationTabEffect: SharedFlow<MainUiEffect> = _effect.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(dispatcher.io) {
            val appConfig = configRepository.getAppConfig()
            updateState {
                it.copy(
                    defaultTabIndex = appConfig.defaultMainTabIndex,
                    selectedTabIndex = appConfig.defaultMainTabIndex,
                    bottomNavigationTabs = DEFAULT_BOTTOM_NAVIGATION_TABS
                )
            }
        }
    }

    fun onTabPressed(tab: BottomNavigationTab) {
        viewModelScope.launch(dispatcher.ui) {
            val selectedTabIndex = state.value.bottomNavigationTabs.indexOf(tab)
            if (state.value.selectedTabIndex == selectedTabIndex) {
                _effect.emit(tab.onTabReselectedEffect)
            } else {
                updateState { it.copy(selectedTabIndex = it.bottomNavigationTabs.indexOf(tab)) }
                _effect.emit(tab.onTabSelectedEffect)
            }
        }
    }

    companion object {
        val DEFAULT_BOTTOM_NAVIGATION_TABS = listOf(
            BottomNavigationTab.HOME,
            BottomNavigationTab.ANIME_LIST,
            BottomNavigationTab.MANGA_LIST
        )
    }
}

data class MainUiState(
    val defaultTabIndex: Int = 0,
    val selectedTabIndex: Int = 0,
    val bottomNavigationTabs: List<BottomNavigationTab> = MainViewModel.DEFAULT_BOTTOM_NAVIGATION_TABS
)

sealed interface MainUiEffect {
    object NavigateToHome : MainUiEffect
    object NavigateToAnimeList : MainUiEffect
    object NavigateToMangaList : MainUiEffect
    object ScrollHomeToTop : MainUiEffect
    object ScrollAnimeListToTop : MainUiEffect
    object ScrollMangaListToTop : MainUiEffect
}