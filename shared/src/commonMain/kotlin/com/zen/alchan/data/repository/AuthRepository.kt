package com.zen.alchan.data.repository

import com.zen.alchan.data.model.api.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val loginTrigger: Flow<String>

    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): User
    suspend fun getLocalCurrentUser(): User
    suspend fun triggerLogin(token: String)
    suspend fun login(token: String): User
}