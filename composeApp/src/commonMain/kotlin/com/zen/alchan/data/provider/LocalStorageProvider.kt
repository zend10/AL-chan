package com.zen.alchan.data.provider

interface LocalStorageProvider {
    suspend fun getLandingCompleted(): Boolean
    suspend fun setLandingCompleted()
}