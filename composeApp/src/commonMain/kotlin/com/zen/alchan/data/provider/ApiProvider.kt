package com.zen.alchan.data.provider

import com.zen.alchan.data.model.api.TrendingMedia

interface ApiProvider {
    suspend fun getTrending(): TrendingMedia
}