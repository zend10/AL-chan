package com.zen.alchan.data.model.api

import com.zen.alchan.data.model.News
import kotlinx.serialization.Serializable

@Serializable
data class HomeData(
    val trendingAnime: HomeDataMedia = HomeDataMedia(),
    val trendingManga: HomeDataMedia = HomeDataMedia(),
    val newAnime: HomeDataMedia = HomeDataMedia(),
    val newManga: HomeDataMedia = HomeDataMedia()
) {
    fun getNews(): List<News> {
        return (trendingAnime.media + trendingManga.media + newAnime.media + newManga.media)
            .map { News(id = it.id, media = it) }
    }
}

@Serializable
data class HomeDataMedia(
    val media: List<Media> = listOf()
)