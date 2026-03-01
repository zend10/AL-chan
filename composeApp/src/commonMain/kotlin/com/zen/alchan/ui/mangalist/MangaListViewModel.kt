package com.zen.alchan.ui.mangalist

import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher

class MangaListViewModel(
    dispatcher: Dispatcher,
) : BaseViewModel<MangaListUiState, MangaListUiEffect>(
    MangaListUiState(), dispatcher
) {
    init {
        loadData()
    }

    private fun loadData() {

    }
}

data class MangaListUiState(
    val isLoading: Boolean = false,
)

sealed interface MangaListUiEffect {
    class NavigateToDetail(val id: Int) : MangaListUiEffect
}