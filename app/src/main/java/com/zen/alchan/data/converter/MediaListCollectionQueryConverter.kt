package com.zen.alchan.data.converter

import com.zen.alchan.MediaListCollectionQuery
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.data.response.Genre

fun MediaListCollectionQuery.Data.convert(): MediaListCollection {
    return MediaListCollection(
        lists = MediaListCollection?.lists?.mapNotNull {
            MediaListGroup(
                entries = it?.entries?.mapNotNull { entry -> entry?.convert() } ?: listOf(),
                name = it?.name ?: "",
                isCustomList = it?.isCustomList ?: false,
                isSplitCompletedList = it?.isSplitCompletedList ?: false,
                status = it?.status
            )
        } ?: listOf()
    )
}

private fun MediaListCollectionQuery.Entry?.convert(): MediaList {
    if (this == null) return MediaList()

    return MediaList(
        id = id,
        status = status,
        score = score ?: 0.0,
        progress = progress ?: 0,
        progressVolumes = progressVolumes,
        repeat = repeat ?: 0,
        priority = priority ?: 0,
        private = private ?: false,
        notes = notes ?: "",
        hiddenFromStatusLists = hiddenFromStatusLists ?: false,
        customLists = customLists,
        advancedScores = advancedScores,
        startedAt = if (startedAt?.year != null) {
            FuzzyDate(startedAt.year, startedAt.month, startedAt.day)
        } else {
            null
        },
        completedAt = if (completedAt?.year != null) {
            FuzzyDate(completedAt.year, completedAt.month, completedAt.day)
        } else {
            null
        },
        updatedAt = updatedAt ?: 0,
        createdAt = createdAt ?: 0,
        media = Media(
            idAniList = media?.id ?: 0,
            idMal = media?.idMal,
            title = MediaTitle(
                romaji = media?.title?.romaji ?: "",
                english = media?.title?.english ?: "",
                native = media?.title?.native ?: "",
                userPreferred = media?.title?.userPreferred ?: "",
            ),
            type = media?.type,
            format = media?.format,
            status = media?.status,
            startDate = if (media?.startDate?.year != null) {
                FuzzyDate(media.startDate.year, media.startDate.month, media.startDate.day)
            } else {
                null
            },
            endDate = if (media?.endDate?.year != null) {
                FuzzyDate(media.endDate.year, media.endDate.month, media.endDate.day)
            } else {
                null
            },
            season = media?.season,
            seasonYear = media?.seasonYear,
            episodes = media?.episodes,
            duration = media?.duration,
            chapters = media?.chapters,
            volumes = media?.volumes,
            countryOfOrigin = media?.countryOfOrigin,
            isLicensed = media?.isLicensed,
            source = media?.source,
            coverImage = MediaCoverImage(
                media?.coverImage?.extraLarge ?: "",
                media?.coverImage?.large ?: "",
                media?.coverImage?.medium ?: ""
            ),
            bannerImage = media?.bannerImage ?: "",
            genres = media?.genres?.mapNotNull { Genre(it ?: "") } ?: listOf(),
            synonyms = media?.synonyms?.filterNotNull() ?: listOf(),
            averageScore = media?.averageScore ?: 0,
            meanScore = media?.meanScore ?: 0,
            popularity = media?.popularity ?: 0,
            trending = media?.trending ?: 0,
            favourites = media?.favourites ?: 0,
            tags = media?.tags?.mapNotNull {
                MediaTag(
                    id = it?.id ?: 0,
                    rank = it?.rank ?: 0,
                )
            } ?: listOf(),
            isFavourite = media?.isFavourite ?: false,
            isAdult = media?.isAdult ?: false,
            nextAiringEpisode = if (media?.nextAiringEpisode != null) {
                AiringSchedule(
                    id = media.nextAiringEpisode.id,
                    airingAt = media.nextAiringEpisode.airingAt,
                    timeUntilAiring = media.nextAiringEpisode.timeUntilAiring,
                    episode = media.nextAiringEpisode.episode
                )
            } else {
                null
            },
            airingSchedule = AiringScheduleConnection(
                nodes = media?.airingSchedule?.nodes?.filterNotNull()?.map {
                    AiringSchedule(
                        id = it.id,
                        airingAt = it.airingAt,
                        timeUntilAiring = it.timeUntilAiring,
                        episode = it.episode
                    )
                } ?: listOf()
            ),
            externalLinks = media?.externalLinks?.mapNotNull {
                MediaExternalLink(
                    siteId = it?.siteId ?: 0
                )
            } ?: listOf(),
            siteUrl = media?.siteUrl ?: ""
        )
    )
}