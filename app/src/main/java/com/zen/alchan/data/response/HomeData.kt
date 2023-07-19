package com.zen.alchan.data.response

import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Review

data class HomeData(
    val trendingAnime: List<Media> = listOf(),
    val trendingManga: List<Media> = listOf(),
    val newAnime: List<Media> = listOf(),
    val newManga: List<Media> = listOf()
)