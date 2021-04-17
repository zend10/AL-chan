package com.zen.alchan.data.model

data class HomeData(
    val trendingAnime: List<Media> = listOf(),
    val trendingManga: List<Media> = listOf(),
    val newAnime: List<Media> = listOf(),
    val newManga: List<Media> = listOf(),
    val review: List<Review> = listOf()
)