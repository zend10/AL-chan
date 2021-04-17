package com.zen.alchan.data.model

import type.MediaFormat
import type.MediaStatus
import type.MediaType

data class Media(
    val idAniList: Int = 0,
    val idMal: Int? = null,
    val title: MediaTitle = MediaTitle(),
    val type: MediaType? = null,
    val format: MediaFormat? = null,
    val status: MediaStatus? = null,
    val description: String = "",
    val startDate: FuzzyDate? = null,
    val endDate: FuzzyDate? = null,
//    val season: MediaSeason? = null,
    val seasonYear: Int? = null,
    val episodes: Int? = null,
    val duration: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
//    val countryOfOrigin: CountryCode? = null,
//    val source: MediaSource? = null,
//    val trailer: MediaTrailer? = null,
    val coverImage: MediaCoverImage = MediaCoverImage(),
    val bannerImage: String = "",
    val genres: List<String> = listOf(),
    val synonyms: List<String> = listOf(),
    val averageScore: Int = 0,
    val meanScore: Int = 0,
    val popularity: Int = 0,
    val trending: Int = 0,
    val favourites: Int = 0,
//    val tags: List<MediaTag> = listOf(),
    val isFavourite: Boolean = false,
    val isAdult: Boolean = false
)