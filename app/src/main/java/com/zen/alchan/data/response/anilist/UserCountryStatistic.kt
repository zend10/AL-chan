package com.zen.alchan.data.response.anilist

data class UserCountryStatistic(
    override val count: Int = 0,
    override val meanScore: Double = 0.0,
    override val minutesWatched: Int = 0,
    override val chaptersRead: Int = 0,
    override val mediaIds: List<Int> = listOf(),
    val country: String = ""
) : UserStatisticsDetail