package com.zen.alchan.helper.pojo

import com.zen.alchan.helper.enums.StatsCategory
import type.MediaFormat
import type.MediaType

class UserStatsData(
    val statsCategory: StatsCategory,
    val mediaType: MediaType,
    val color: Int? = null,
    val count: Int? = null,
    val meanScore: Double? = null,
    val minutesWatched: Int? = null,
    val chaptersRead: Int? = null,
    val mediaIds: List<Int?>? = null,
    val label: String? = null
)