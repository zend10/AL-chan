package com.zen.alchan.data.provider

import com.zen.alchan.data.model.api.User

interface LocalStorageProvider {
    suspend fun getLandingCompleted(): Boolean
    suspend fun setLandingCompleted()
    suspend fun getAniListToken(): String
    suspend fun setAniListToken(token: String)
    suspend fun getCurrentAniListUser(): User
    suspend fun setCurrentAniListUser(user: User)
}