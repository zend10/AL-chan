package com.zen.alchan.data.response.anilist

import com.zen.alchan.type.MediaFormat
import com.zen.alchan.type.MediaRankType
import com.zen.alchan.type.MediaSeason


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