package com.zen.alchan.data.repository

import com.zen.alchan.data.model.api.User
import com.zen.alchan.data.provider.ApiProvider
import com.zen.alchan.data.provider.LocalStorageProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class DefaultAuthRepository(
    private val localStorageProvider: LocalStorageProvider,
    private val apiProvider: ApiProvider
) : AuthRepository {

    private val _loginTrigger = MutableSharedFlow<String>()
    override val loginTrigger: Flow<String>
        get() = _loginTrigger

    override suspend fun isLoggedIn(): Boolean {
        return localStorageProvider.getAniListToken().isNotBlank()
    }

    override suspend fun getCurrentUser(): User {
        val token = localStorageProvider.getAniListToken()
        val user = apiProvider.getCurrentUser(token).toModel()
        localStorageProvider.setCurrentAniListUser(user)
        return user
    }

    override suspend fun getLocalCurrentUser(): User {
        val user = localStorageProvider.getCurrentAniListUser()
        return user
    }

    override suspend fun triggerLogin(token: String) {
        _loginTrigger.emit(token)
    }

    override suspend fun login(token: String): User {
        localStorageProvider.setAniListToken(token)
        val user = apiProvider.getCurrentUser(token).toModel()
        localStorageProvider.setCurrentAniListUser(user)
        return user
    }
}