package com.zen.alchan.data.provider

import com.zen.alchan.data.network.KtorHttpClient
import com.zen.alchan.data.request.graphql.HOME_DATA_QUERY
import com.zen.alchan.data.response.anilist.HomeDataResponse
import com.zen.alchan.helper.AniListConstant
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

class AniListApiProvider(
    private val httpClient: KtorHttpClient,
    private val aniListConstant: AniListConstant
) : ApiProvider {
    override suspend fun getHomeData(): HomeDataResponse {
        try {
            val result = httpClient.query<HomeDataResponse>(
                HOME_DATA_QUERY,
                mapOf(getStatusVersionVariable())
            )
            return result
        } catch (exception: Exception) {
            throw exception
        }
    }

    private fun getStatusVersionVariable(): Pair<String, JsonElement> {
        return "statusVersion" to JsonPrimitive(aniListConstant.ANILIST_STATUS_VERSION)
    }

    private fun handleException(exception: Exception) {

    }
}