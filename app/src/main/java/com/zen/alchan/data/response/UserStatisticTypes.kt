package com.zen.alchan.data.response

data class UserStatisticTypes(
    val anime: UserStatistics = UserStatistics(),
    val manga: UserStatistics = UserStatistics()
)