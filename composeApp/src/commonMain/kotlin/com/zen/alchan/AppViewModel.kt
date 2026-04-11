package com.zen.alchan

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.helper.DeeplinkConstant
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.coroutines.launch

class AppViewModel(
    dispatcher: Dispatcher,
    private val authRepository: AuthRepository,
    private val deeplinkConstant: DeeplinkConstant
) : BaseViewModel<Unit, AppUiEffect>(Unit, dispatcher) {

    fun onDeeplinkReceived(deeplink: String) {
        viewModelScope.launch(dispatcher.io) {
            if (deeplink.contains("${deeplinkConstant.LOGIN}#")) {
                try {
                    val queryParamsString = deeplink.drop("${deeplinkConstant.LOGIN}#".length)
                    val queryParams = mutableMapOf<String, String>()
                    queryParamsString.split("&").forEach { queryParamString ->
                        queryParamString.split("=").let {
                            if (it.size == 2) {
                                val key = it[0]
                                val value = it[1]
                                queryParams[key] = value
                            }
                        }
                    }
                    val token = queryParams["access_token"] ?: ""
                    val tokenExpiry = queryParams["expires_in"]
                    authRepository.triggerLogin(token)
                } catch (e: Exception) {

                }
            }
        }
    }
}

sealed interface AppUiEffect {
    object NavigateToSeasonal : AppUiEffect
}