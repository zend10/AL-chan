package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
class TrendingMedia(
    val trendingAnime: Page<List<Media>>,
)

@Serializable
class TrendingMediaItem(
    val media: List<Media>
)