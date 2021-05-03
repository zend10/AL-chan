package com.zen.alchan.data.response.anilist

data class UserStatistics(
    val count: Int = 0,
    val meanScore: Double = 0.0,
    val standardDeviation: Double = 0.0,
    val minutesWatched: Int = 0,
    val episodesWatched: Int = 0,
    val chaptersRead: Int = 0,
    val volumesRead: Int = 0,
    val formats: List<UserFormatStatistic> = listOf(),
    val statuses: List<UserStatusStatistic> = listOf(),
    val scores: List<UserScoreStatistic> = listOf(),
    val lengths: List<UserLengthStatistic> = listOf(),
    val releaseYears: List<UserReleaseYearStatistic> = listOf(),
    val startYears: List<UserStartYearStatistic> = listOf(),
    val genres: List<UserGenreStatistic> = listOf(),
    val tags: List<UserTagStatistic> = listOf(),
//    val countries: List<UserCountryStatistic> = listOf(),
    val voiceActors: List<UserVoiceActorStatistic> = listOf(),
    val staffs: List<UserStaffStatistic> = listOf(),
    val studios: List<UserStudioStatistic> = listOf()
)