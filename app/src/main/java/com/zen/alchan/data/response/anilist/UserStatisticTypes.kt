package com.zen.alchan.data.response.anilist

data class UserStatisticTypes(
    val anime: UserStatistics = UserStatistics(),
    val manga: UserStatistics = UserStatistics()
)