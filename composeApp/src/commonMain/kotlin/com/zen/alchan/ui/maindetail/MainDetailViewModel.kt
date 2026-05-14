package com.zen.alchan.ui.maindetail

import com.zen.alchan.data.enums.DetailPage
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.serialization.Serializable

class MainDetailViewModel(
    dispatcher: Dispatcher,
    private val mainDetailParam: MainDetailParam
) : BaseViewModel<MainDetailUiState, MainDetailUiEffect>(MainDetailUiState(), dispatcher) {

    init {
        loadData()
    }

    private fun loadData() {
        sendNewEffect(
            when (mainDetailParam.startDestination) {
                DetailPage.USER -> MainDetailUiEffect.NavigateToUser(mainDetailParam.id)
                DetailPage.MEDIA_DETAIL -> MainDetailUiEffect.NavigateToMediaDetail(mainDetailParam.id)
            }
        )
    }
}

@Serializable
data class MainDetailParam(
    val startDestination: DetailPage,
    val id: String
)

data class MainDetailUiState(
    val currentPage: DetailPage? = null
)

sealed interface MainDetailUiEffect {
    class NavigateToUser(val id: String) : MainDetailUiEffect
    class NavigateToMediaDetail(val id: String) : MainDetailUiEffect
}
