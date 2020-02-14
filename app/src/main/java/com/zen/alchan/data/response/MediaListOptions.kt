package com.zen.alchan.data.response

import type.ScoreFormat

class MediaListOptions(
    val scoreFormat: ScoreFormat?,
    val rowOrder: String?,
    val animeList: MediaListTypeOptions?,
    val mangaList: MediaListTypeOptions?
)