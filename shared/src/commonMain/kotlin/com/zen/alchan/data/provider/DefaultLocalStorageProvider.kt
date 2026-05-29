package com.zen.alchan.data.provider

import com.zen.alchan.data.model.api.User

class DefaultLocalStorageProvider : LocalStorageProvider {
    override suspend fun getLandingCompleted(): Boolean {
        return false
    }

    override suspend fun setLandingCompleted() {

    }

    override suspend fun getAniListToken(): String {
        return ""
    }

    override suspend fun setAniListToken(token: String) {

    }

    override suspend fun getCurrentAniListUser(): User {
        return User()
    }

    override suspend fun setCurrentAniListUser(user: User) {

    }
}