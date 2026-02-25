package com.zen.alchan.data.repository

import com.zen.alchan.data.model.AppConfig

class DefaultConfigRepository : ConfigRepository {

    override suspend fun getAppConfig(): AppConfig {
        return AppConfig()
    }
}