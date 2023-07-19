package com.zen.alchan.data.response.anilist

data class AiringSchedule(
    val id: Int = 0,
    val airingAt: Int = 0,
    val timeUntilAiring: Int = 0,
    val episode: Int = 0,
    val media: Media = Media()
)