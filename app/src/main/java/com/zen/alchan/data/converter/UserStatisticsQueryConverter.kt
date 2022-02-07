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
        formats = this?.formats?.mapNotNull {
            UserFormatStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                format = it?.format
            )
        } ?: listOf(),
        statuses = this?.statuses?.mapNotNull {
            UserStatusStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                status = it?.status
            )
        } ?: listOf(),
        scores = this?.scores?.mapNotNull {
            UserScoreStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                score = it?.score ?: 0
            )
        } ?: listOf(),
        lengths = this?.lengths?.mapNotNull {
            UserLengthStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                length = it?.length ?: ""
            )
        } ?: listOf(),
        releaseYears = this?.releaseYears?.mapNotNull {
            UserReleaseYearStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                releaseYear = it?.releaseYear ?: 0
            )
        } ?: listOf(),
        startYears = this?.startYears?.mapNotNull {
            UserStartYearStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                startYear = it?.startYear ?: 0
            )
        } ?: listOf(),
        genres = this?.genres?.mapNotNull {
            UserGenreStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                genre = it?.genre ?: ""
            )
        } ?: listOf(),
        tags = this?.tags?.mapNotNull {
            UserTagStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                tag = MediaTag(
                    id = it?.tag?.id ?: 0,
                    name = it?.tag?.name ?: ""
                )
            )
        } ?: listOf(),
        countries = this?.countries?.mapNotNull {
            UserCountryStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                country = it?.country ?: ""
            )
        } ?: listOf(),
        voiceActors = this?.voiceActors?.mapNotNull {
            UserVoiceActorStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                voiceActor = Staff(
                    id = it?.voiceActor?.id ?: 0,
                    name = StaffName(
                        first = it?.voiceActor?.name?.first ?: "",
                        middle = it?.voiceActor?.name?.middle ?: "",
                        last = it?.voiceActor?.name?.last ?: "",
                        full = it?.voiceActor?.name?.full ?: "",
                        native = it?.voiceActor?.name?.native_ ?: "",
                        alternative = it?.voiceActor?.name?.alternative?.filterNotNull() ?: listOf(),
                        userPreferred = it?.voiceActor?.name?.userPreferred ?: "",
                    )
                ),
                characterIds = it?.characterIds?.filterNotNull() ?: listOf()
            )
        } ?: listOf(),
        staffs = this?.staff?.mapNotNull {
            UserStaffStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                staff = Staff(
                    id = it?.staff?.id ?: 0,
                    name = StaffName(
                        first = it?.staff?.name?.first ?: "",
                        middle = it?.staff?.name?.middle ?: "",
                        last = it?.staff?.name?.last ?: "",
                        full = it?.staff?.name?.full ?: "",
                        native = it?.staff?.name?.native_ ?: "",
                        alternative = it?.staff?.name?.alternative?.filterNotNull() ?: listOf(),
                        userPreferred = it?.staff?.name?.userPreferred ?: "",
                    )
                ),
            )
        } ?: listOf(),
        studios = this?.studios?.mapNotNull {
            UserStudioStatistic(
                count = it?.count ?: 0,
                meanScore = it?.meanScore ?: 0.0,
                minutesWatched = it?.minutesWatched ?: 0,
                chaptersRead = it?.chaptersRead ?: 0,
                mediaIds = it?.mediaIds?.filterNotNull() ?: listOf(),
                studio = Studio(
                    id = it?.studio?.id ?: 0,
                    name = it?.studio?.name ?: "",
                    isAnimationStudio = it?.studio?.isAnimationStudio ?: false
                )
            )
        } ?: listOf(),
    )
}