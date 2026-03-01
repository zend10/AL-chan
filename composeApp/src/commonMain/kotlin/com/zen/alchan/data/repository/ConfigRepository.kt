package com.zen.alchan.data.repository

import com.zen.alchan.data.model.AppConfig
import com.zen.alchan.data.model.RemoteConfig

interface ConfigRepository {
    suspend fun getRemoteConfig(): RemoteConfig
    suspend fun getAppConfig(): AppConfig
}