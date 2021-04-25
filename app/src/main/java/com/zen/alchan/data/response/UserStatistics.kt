package com.zen.alchan.data.response

data class UserStatistics(
    val count: Int = 0,
    val meanScore: Double = 0.0,
    val standardDeviation: Double = 0.0,
    val minutesWatched: Int = 0,
    val episodesWatched: Int = 0,
    val chaptersRead: Int = 0,
    val volumesRead: Int = 0
)