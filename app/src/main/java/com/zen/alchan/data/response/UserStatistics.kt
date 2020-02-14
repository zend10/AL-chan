package com.zen.alchan.data.response

class UserStatistics(
    val count: Int,
    val meanScore: Double,
    val standardDeviation: Double,
    val minutesWatched: Int,
    val episodesWatched: Int,
    val chaptersRead: Int,
    val volumesRead: Int
)