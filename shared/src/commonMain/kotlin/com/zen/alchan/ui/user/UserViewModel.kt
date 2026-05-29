package com.zen.alchan.ui.user

import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.api.User
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.ConfigRepository
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.base.Dispatcher
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class UserViewModel(
    dispatcher: Dispatcher,
    private val userParam: UserParam,
    private val configRepository: ConfigRepository,
    private val authRepository: AuthRepository
) : BaseViewModel<UserUiState, UserUiEffect>(UserUiState(), dispatcher) {

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(dispatcher.io) {
            val appConfig = configRepository.getAppConfig()
            var user = authRepository.getLocalCurrentUser()

            if (user.id != userParam.id) {
                // call API
                user = authRepository.getCurrentUser()
            }

            updateState { it.copy(user = user, appConfig = appConfig) }
        }
    }
}

@Serializable
data class UserParam(
    val id: String = ""
)

data class UserUiState(
    val isLoading: Boolean = false,
    val user: User = User(),
    val appConfig: AppConfig = AppConfig()
)

sealed interface UserUiEffect {

}