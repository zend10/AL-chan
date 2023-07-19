package com.zen.alchan.data.response.anilist

interface UserStatisticsDetail {
    val count: Int
    val meanScore: Double
    val minutesWatched: Int
    val chaptersRead: Int
    val mediaIds: List<Int>
}