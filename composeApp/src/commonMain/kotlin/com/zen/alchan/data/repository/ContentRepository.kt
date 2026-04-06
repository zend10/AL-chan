package com.zen.alchan.data.repository

import com.zen.alchan.data.model.api.HomeData

interface ContentRepository {
    suspend fun getHomeData(): HomeData
}