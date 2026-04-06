package com.zen.alchan.data.provider

import com.zen.alchan.data.model.api.HomeData
import com.zen.alchan.data.response.anilist.HomeDataResponse

interface ApiProvider {
    suspend fun getHomeData(): HomeDataResponse
}