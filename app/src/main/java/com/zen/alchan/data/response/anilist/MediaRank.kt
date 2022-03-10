package com.zen.alchan.data.response.anilist

import type.MediaFormat
import type.MediaRankType
import type.MediaSeason

data class MediaRank(
    val id: Int = 0,
    val rank: Int = 0,
    val type: MediaRankType? = null,
    val format: MediaFormat? = null,
    val year: Int = 0,
    val season: MediaSeason? = null,
    val allTime: Boolean = false,
    val context: String = ""
)