package com.zen.alchan.data.provider

import com.zen.alchan.data.model.api.TrendingMedia
import com.zen.alchan.data.network.KtorHttpClient
import com.zen.alchan.data.request.graphql.TRENDING_MEDIA

class AniListApiProvider(private val httpClient: KtorHttpClient) : ApiProvider {
    override suspend fun getTrending(): TrendingMedia {
        return httpClient.query<TrendingMedia>(TRENDING_MEDIA, mapOf())
    }
}