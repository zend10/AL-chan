package com.zen.alchan.data.repository

import com.zen.alchan.data.model.api.HomeData
import com.zen.alchan.data.provider.ApiProvider

class DefaultContentRepository(private val apiProvider: ApiProvider) : ContentRepository {
    override suspend fun getHomeData(): HomeData {
        val homeData = apiProvider.getHomeData().toModel()
        return homeData
    }
}