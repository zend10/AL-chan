package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class HomeData(
    val trendingAnime: HomeDataMedia = HomeDataMedia(),
    val trendingManga: HomeDataMedia = HomeDataMedia(),
    val newAnime: HomeDataMedia = HomeDataMedia(),
    val newManga: HomeDataMedia = HomeDataMedia()
)

@Serializable
data class HomeDataMedia(
    val media: List<Media> = listOf()
)