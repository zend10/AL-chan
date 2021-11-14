package com.zen.alchan.data.converter

import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.*

fun MediaQuery.Data.convert(): Media {
    return Media(
        idAniList = media?.id ?: 0,
        idMal = media?.idMal,
        title = MediaTitle(
            romaji = media?.title?.romaji ?: "",
            english = media?.title?.english ?: "",
            native = media?.title?.native_ ?: "",
            userPreferred = media?.title?.userPreferred ?: ""
        ),
        type = media?.type,
        format = media?.format,
        status = media?.status,
        description = media?.description ?: "",
        startDate = if (media?.startDate != null)
            FuzzyDate(year = media.startDate.year, month = media.startDate.month, day = media.startDate.day)
        else
            null,
        endDate = if (media?.endDate != null)
            FuzzyDate(year = media.endDate.year, month = media.endDate.month, day = media.endDate.day)
        else
            null,
        season = media?.season,
        seasonYear = media?.seasonYear,
        episodes = media?.episodes,
        duration = media?.duration,
        chapters = media?.chapters,
        volumes = media?.volumes,
        countryOfOrigin = media?.countryOfOrigin,
        isLicensed = media?.isLicensed,
        source = media?.source,
//        trailer
        coverImage = MediaCoverImage(
            extraLarge = media?.coverImage?.extraLarge ?: "",
            large = media?.coverImage?.large ?: "",
            medium = media?.coverImage?.medium ?: ""
        ),
        bannerImage = media?.bannerImage ?: "",
        genres = media?.genres?.filterNotNull()?.map { Genre(name = it) } ?: listOf(),
        synonyms = media?.synonyms?.filterNotNull() ?: listOf(),
        averageScore = media?.averageScore ?: 0,
        meanScore = media?.meanScore ?: 0,
        popularity = media?.popularity ?: 0,
        trending = media?.trending ?: 0,
        favourites = media?.favourites ?: 0,
        tags = media?.tags?.filterNotNull()?.map {
            MediaTag(
                id = it.id,
                name = it.name,
                description = it.description ?: "",
                category = it.category ?: "",
                rank = it.rank ?: 0,
                isGeneralSpoiler = it.isGeneralSpoiler ?: false,
                isMediaSpoiler = it.isMediaSpoiler ?: false,
                isAdult = it.isAdult ?: false
            )
        } ?: listOf(),
//        relations
        characters = CharacterConnection(
            nodes = media?.characters?.nodes?.filterNotNull()?.map {
                Character(
                    id = it.id,
                    name = CharacterName(
                        first = it.name?.first ?: "",
                        middle = it.name?.middle ?: "",
                        last = it.name?.last ?: "",
                        full = it.name?.full ?: "",
                        native = it.name?.native_ ?: "",
                        alternative = it.name?.alternative?.filterNotNull() ?: listOf(),
                        alternativeSpoiler = it.name?.alternativeSpoiler?.filterNotNull() ?: listOf(),
                        userPreferred = it.name?.userPreferred ?: "",
                    ),
                    image = CharacterImage(
                        large = it.image?.large ?: "",
                        medium = it.image?.medium ?: ""
                    )
                )
            } ?: listOf()
        ),
        isFavourite = media?.isFavourite ?: false,
        isAdult = media?.isAdult ?: false,
        nextAiringEpisode = if (media?.nextAiringEpisode != null)
            AiringSchedule(
                id = media.nextAiringEpisode.id,
                airingAt = media.nextAiringEpisode.airingAt,
                timeUntilAiring = media.nextAiringEpisode.timeUntilAiring,
                episode = media.nextAiringEpisode.episode
            )
        else
            null
        ,
        externalLinks = media?.externalLinks?.filterNotNull()?.map {
            MediaExternalLink(id = it.id, url = it.url, site = it.site)
        } ?: listOf(),
        siteUrl = media?.siteUrl ?: ""
    )
}