package com.zen.alchan.data.repository

import com.zen.alchan.data.model.AppConfig

interface ConfigRepository {
    suspend fun getAppConfig(): AppConfig
}