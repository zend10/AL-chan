package com.zen.alchan.data.repository

import com.zen.alchan.data.model.User

interface AuthRepository {
    suspend fun getCurrentUser(): User
}