package com.zen.alchan.data.response

import type.*

class Media(
    val id: Int,
    val title: MediaTitle?,
    val type: MediaType?,
    val format: MediaFormat?,
    val status: MediaStatus?,
    val startDate: FuzzyDate?,
    val season: MediaSeason?,
    val seasonYear: Int?,
    val episodes: Int?,
    val chapters: Int?,
    val volumes: Int?,
    val countryOfOrigin: String?,
    val source: MediaSource?,
    var isFavourite: Boolean,
    val coverImage: MediaCoverImage?,
    val genres: List<String>?,
    val averageScore: Int?,
    val popularity: Int?,
    val isAdult: Boolean?,
    var nextAiringEpisode: AiringSchedule?,
    val siteUrl: String?
)