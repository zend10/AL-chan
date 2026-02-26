package com.zen.alchan.data.repository

import com.zen.alchan.data.model.User

class DefaultAuthRepository : AuthRepository {

    override suspend fun isLoggedIn(): Boolean {
        return false
    }

    override suspend fun getCurrentUser(): User {
        return User()
    }
}