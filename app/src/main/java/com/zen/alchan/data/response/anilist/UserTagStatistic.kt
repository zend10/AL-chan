package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.response.anilist.MediaTag

data class UserTagStatistic(
    val count: Int = 0,
    val meanScore: Double = 0.0,
    val minutesWatched: Int = 0,
    val chaptersRead: Int = 0,
    val mediaIds: List<Int> = listOf(),
    val tag: MediaTag? = null
)