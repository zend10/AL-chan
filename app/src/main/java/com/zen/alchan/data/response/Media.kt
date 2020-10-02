package com.zen.alchan.data.response

import com.zen.alchan.helper.pojo.MediaTags
import type.*

class Media(
    val id: Int,
    val title: MediaTitle? = null,
    val type: MediaType? = null,
    val format: MediaFormat? = null,
    val status: MediaStatus? = null,
    val startDate: FuzzyDate? = null,
    val season: MediaSeason? = null,
    val seasonYear: Int? = null,
    val episodes: Int? = null,
    val duration: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val countryOfOrigin: String? = null,
    val source: MediaSource? = null,
    var isFavourite: Boolean? = null,
    val coverImage: MediaCoverImage? = null,
    val bannerImage: String? = null,
    val genres: List<String?>? = null,
    val synonyms: List<String?>? = null,
    val averageScore: Int? = null,
    val popularity: Int? = null,
    val tags: List<MediaTag?>? = null,
    val isAdult: Boolean? = null,
    var nextAiringEpisode: AiringSchedule? = null,
    val externalLinks: List<MediaExternalLinks?>? = null,
    val siteUrl: String? = null
)