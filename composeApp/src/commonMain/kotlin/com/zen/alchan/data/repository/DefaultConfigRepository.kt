package com.zen.alchan.data.repository

import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.RemoteConfig
import com.zen.alchan.data.provider.LocalStorageProvider

class DefaultConfigRepository(private val localStorageProvider: LocalStorageProvider) : ConfigRepository {

    override suspend fun getRemoteConfig(): RemoteConfig {
        return RemoteConfig()
    }

    override suspend fun getAppConfig(): AppConfig {
        return AppConfig()
    }

    override suspend fun getLandingCompleted(): Boolean {
        return localStorageProvider.getLandingCompleted()
    }

    override suspend fun setLandingCompleted() {
        localStorageProvider.setLandingCompleted()
    }
}