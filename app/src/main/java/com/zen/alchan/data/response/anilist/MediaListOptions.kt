package com.zen.alchan.data.response.anilist

import type.ScoreFormat

data class MediaListOptions(
    val scoreFormat: ScoreFormat? = null,
    val rowOrder: String = "",
    val animeList: MediaListTypeOptions = MediaListTypeOptions(),
    val mangaList: MediaListTypeOptions = MediaListTypeOptions()
)