package com.zen.alchan.data.repository

import com.zen.alchan.data.model.User

interface AuthRepository {
    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): User
}