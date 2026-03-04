package com.zen.alchan.data.repository

import com.zen.alchan.data.model.api.Media
import com.zen.alchan.data.provider.ApiProvider

class DefaultContentRepository(private val apiProvider: ApiProvider) : ContentRepository {
    override suspend fun getTrending(): List<Media> {
        return apiProvider.getTrending().trendingAnime.data
    }
}