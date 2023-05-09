package com.zen.alchan.data.response.anilist

import com.zen.alchan.type.ScoreFormat


data class MediaListOptions(
    var scoreFormat: ScoreFormat? = null,
    var rowOrder: String = "",
    val animeList: MediaListTypeOptions = MediaListTypeOptions(),
    val mangaList: MediaListTypeOptions = MediaListTypeOptions()
)