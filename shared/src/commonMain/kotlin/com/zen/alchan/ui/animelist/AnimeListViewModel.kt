package com.zen.alchan.ui.animelist

import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher

class AnimeListViewModel(
    dispatcher: Dispatcher,
) : BaseViewModel<AnimeListUiState, AnimeListUiEffect>(
    AnimeListUiState(), dispatcher
) {
    init {
        loadData()
    }

    private fun loadData() {

    }
}

data class AnimeListUiState(
    val isLoading: Boolean = false,
)

sealed interface AnimeListUiEffect {
    class NavigateToDetail(val id: Int) : AnimeListUiEffect
}