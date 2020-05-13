package com.zen.alchan.data.response

import type.MediaFormat
import type.MediaSource

class SeasonalAnime(
    val id: Int,
    val title: MediaTitle?,
    val coverImage: MediaCoverImage?,
    val format: MediaFormat?,
    val source: MediaSource?,
    val episodes: Int?,
    val averageScore: Int?,
    val favourites: Int?,
    val description: String?,
    val startDate: FuzzyDate?,
    val genres: List<String?>?,
    val studios: StudioConnection?,
    val stats: MediaStats?,
    var mediaListEntry: MediaList?
)