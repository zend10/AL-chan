package com.zen.alchan.data.provider

import com.zen.alchan.data.response.anilist.HomeDataResponse
import com.zen.alchan.data.response.anilist.ViewerResponse

interface ApiProvider {
    fun setBearerToken(token: String)
    suspend fun getHomeData(): HomeDataResponse
    suspend fun getCurrentUser(): ViewerResponse
}