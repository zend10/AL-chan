package com.zen.alchan.data.response.anilist

import com.zen.alchan.type.MediaListStatus


data class UserStatusStatistic(
    override val count: Int = 0,
    override val meanScore: Double = 0.0,
    override val minutesWatched: Int = 0,
    override val chaptersRead: Int = 0,
    override val mediaIds: List<Int> = listOf(),
    val status: MediaListStatus? = null
): UserStatisticsDetail