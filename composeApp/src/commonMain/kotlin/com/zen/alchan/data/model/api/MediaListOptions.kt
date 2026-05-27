package com.zen.alchan.data.model.api

import com.zen.alchan.data.enums.ScoreFormat
import kotlinx.serialization.Serializable

@Serializable
data class MediaListOptions(
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_100,
    val rowOrder: String = "",
    val animeList: MediaListTypeOptions = MediaListTypeOptions(),
    val mangaList: MediaListTypeOptions = MediaListTypeOptions()
)
