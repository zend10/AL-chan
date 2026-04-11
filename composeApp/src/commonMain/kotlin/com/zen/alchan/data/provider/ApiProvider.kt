package com.zen.alchan.data.provider

import com.zen.alchan.data.response.anilist.HomeDataResponse
import com.zen.alchan.data.response.anilist.ViewerResponse

interface ApiProvider {
    suspend fun getHomeData(): HomeDataResponse
    suspend fun getCurrentUser(token: String): ViewerResponse
}