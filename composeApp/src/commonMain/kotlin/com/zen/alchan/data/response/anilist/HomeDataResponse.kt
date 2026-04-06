package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.HomeData
import com.zen.alchan.data.model.api.HomeDataMedia
import kotlinx.serialization.Serializable

@Serializable
data class HomeDataResponse(
    val trendingAnime: HomeDataMediaResponse? = null,
    val trendingManga: HomeDataMediaResponse? = null,
    val newAnime: HomeDataMediaResponse? = null,
    val newManga: HomeDataMediaResponse? = null,
) {
    fun toModel(): HomeData {
        return HomeData(
            trendingAnime = trendingAnime?.toModel() ?: HomeDataMedia(),
            trendingManga = trendingManga?.toModel() ?: HomeDataMedia(),
            newAnime = newAnime?.toModel() ?: HomeDataMedia(),
            newManga = newManga?.toModel() ?: HomeDataMedia(),
        )
    }
}

@Serializable
data class HomeDataMediaResponse(
    val media: List<MediaResponse?>? = null
) {
    fun toModel(): HomeDataMedia {
        return HomeDataMedia(
            media = media?.filterNotNull()?.map {
                it.toModel()
            } ?: listOf()
        )
    }
}