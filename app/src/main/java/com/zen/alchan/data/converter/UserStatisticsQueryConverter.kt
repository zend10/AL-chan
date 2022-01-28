package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*
import fragment.UserStatistics

fun UserStatisticsQuery.Data.convert(): User {
    val user = this.user ?: return User()
    return User(
        id = user.id,
        name = user.name,
        statistics = UserStatisticTypes(
            anime = user.statistics?.anime?.fragments?.userStatistics?.convert() ?: UserStatistics(),
            manga = user.statistics?.manga?.fragments?.userStatistics?.convert() ?: UserStatistics()
        )
    )
}

fun UserStatistics?.convert(): com.zen.alchan.data.response.anilist.UserStatistics {
    return UserStatistics(
        count = this?.count ?: 0,
        meanScore = this?.meanScore ?: 0.0,
        standardDeviation = this?.standardDeviation ?: 0.0,
        minutesWatched = this?.minutesWatched ?: 0,
        episodesWatched = this?.episodesWatched ?: 0,
        chaptersRead = this?.chaptersRead ?: 0,
        volumesRead = this?.volumesRead ?: 0,
        statuses = this?.statuses?.mapNotNull {
            UserStatusStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                status = it?.status
            )
        } ?: listOf(),
        scores = this?.scores?.mapNotNull {
            UserScoreStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                score = it?.score ?: 0
            )
        } ?: listOf(),
        releaseYears = this?.releaseYears?.mapNotNull {
            UserReleaseYearStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                releaseYear = it?.releaseYear ?: 0
            )
        } ?: listOf(),
        startYears = this?.startYears?.mapNotNull {
            UserStartYearStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                startYear = it?.startYear ?: 0
            )
        } ?: listOf(),
        genres = this?.genres?.mapNotNull {
            UserGenreStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                genre = it?.genre ?: ""
            )
        } ?: listOf(),
        tags = this?.tags?.mapNotNull {
            UserTagStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                tag = MediaTag(
                    id = it?.tag?.id ?: 0,
                    name = it?.tag?.name ?: ""
                )
            )
        } ?: listOf(),
        studios = this?.studios?.mapNotNull {
            UserStudioStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                studio = Studio(
                    id = it?.studio?.id ?: 0,
                    name = it?.studio?.name ?: "",
                    isAnimationStudio = it?.studio?.isAnimationStudio ?: false
                )
            )
        } ?: listOf(),
    )
}