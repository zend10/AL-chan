package com.zen.alchan.data.model.api

import com.zen.alchan.data.response.anilist.MediaResponse
import kotlinx.serialization.Serializable

@Serializable
class TrendingMedia(
    val trendingAnime: Page<List<MediaResponse>>,
)

@Serializable
class TrendingMediaItem(
    val media: List<MediaResponse>
)