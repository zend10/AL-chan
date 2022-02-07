package com.zen.alchan.data.response.anilist

data class UserVoiceActorStatistic(
    override val count: Int = 0,
    override val meanScore: Double = 0.0,
    override val minutesWatched: Int = 0,
    override val chaptersRead: Int = 0,
    override val mediaIds: List<Int> = listOf(),
    val voiceActor: Staff? = null,
    val characterIds: List<Int> = listOf()
): UserStatisticsDetail