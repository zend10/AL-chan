package com.zen.alchan.data.repository

import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.RemoteConfig

class DefaultConfigRepository : ConfigRepository {

    override suspend fun getRemoteConfig(): RemoteConfig {
        return RemoteConfig()
    }

    override suspend fun getAppConfig(): AppConfig {
        return AppConfig()
    }
}