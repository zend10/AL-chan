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
        trailer = if (media?.trailer != null)
            MediaTrailer(
                id = media.trailer.id ?: "",
                site = media.trailer.site ?: "",
                thumbnail = media.trailer.thumbnail ?: ""
            )
        else
            null,
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
        relations = MediaConnection(
            edges = media?.relations?.edges?.map {
                MediaEdge(
                    node = Media(
                        idAniList = it?.node?.id ?: 0,
                        title = MediaTitle(
                            romaji = it?.node?.title?.romaji ?: "",
                            english = it?.node?.title?.english ?: "",
                            native = it?.node?.title?.native_ ?: "",
                            userPreferred = it?.node?.title?.userPreferred ?: "",
                        ),
                        type = it?.node?.type,
                        format = it?.node?.format,
                        coverImage = MediaCoverImage(
                            extraLarge = it?.node?.coverImage?.extraLarge ?: "",
                            large = it?.node?.coverImage?.large ?: "",
                            medium = it?.node?.coverImage?.medium ?: ""
                        )
                    ),
                    relationType = it?.relationType
                )
            } ?: listOf()
        ),
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
        staff = StaffConnection(
            edges = media?.staff?.edges?.map {
                StaffEdge(
                    node = Staff(
                        id = it?.node?.id ?: 0,
                        name = StaffName(
                            first = it?.node?.name?.first ?: "",
                            middle = it?.node?.name?.middle ?: "",
                            last = it?.node?.name?.last ?: "",
                            full = it?.node?.name?.full ?: "",
                            native = it?.node?.name?.native_ ?: "",
                            alternative = it?.node?.name?.alternative?.filterNotNull() ?: listOf(),
                            userPreferred = it?.node?.name?.userPreferred ?: "",
                        ),
                        image = StaffImage(
                            large = it?.node?.image?.large ?: "",
                            medium = it?.node?.image?.medium ?: ""
                        )
                    ),
                    id = it?.id ?: 0,
                    role = it?.role ?: ""
                )
            } ?: listOf()
        ),
        studios = StudioConnection(
            edges = media?.studios?.edges?.map {
                StudioEdge(
                    node = Studio(
                        id = it?.node?.id ?: 0,
                        name = it?.node?.name ?: "",
                        isAnimationStudio = it?.node?.isAnimationStudio ?: false
                    ),
                    id = it?.id ?: 0,
                    isMain = it?.isMain ?: false
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
            MediaExternalLink(
                id = it.id,
                url = it.url ?: "",
                site = it.site,
                siteId = it.siteId ?: 0,
                type = it.type,
                language = it.language ?: "",
                color = it.color ?: "",
                icon = it.icon ?: ""
            )
        } ?: listOf(),
        rankings = media?.rankings?.map {
            MediaRank(
                id = it?.id ?: 0,
                rank = it?.rank ?: 0,
                type = it?.type,
                format = it?.format,
                year = it?.year ?: 0,
                season = it?.season,
                allTime = it?.allTime ?: false,
                context = it?.context ?: ""
            )
        } ?: listOf(),
        recommendations = RecommendationConnection(
            nodes = media?.recommendations?.nodes?.map {
                Recommendation(
                    id = it?.id ?: 0,
                    rating = it?.id ?: 0,
                    userRating = it?.userRating,
                    mediaRecommendation = Media(
                        idAniList = it?.mediaRecommendation?.id ?: 0,
                        title = MediaTitle(
                            romaji = it?.mediaRecommendation?.title?.romaji ?: "",
                            english = it?.mediaRecommendation?.title?.english ?: "",
                            native = it?.mediaRecommendation?.title?.native_ ?: "",
                            userPreferred = it?.mediaRecommendation?.title?.userPreferred ?: ""
                        ),
                        format = it?.mediaRecommendation?.format,
                        coverImage = MediaCoverImage(
                            extraLarge = it?.mediaRecommendation?.coverImage?.extraLarge ?: "",
                            large = it?.mediaRecommendation?.coverImage?.large ?: "",
                            medium = it?.mediaRecommendation?.coverImage?.medium ?: ""
                        ),
                        averageScore = it?.mediaRecommendation?.averageScore ?: 0,
                        meanScore = it?.mediaRecommendation?.meanScore ?: 0,
                        popularity = it?.mediaRecommendation?.popularity ?: 0
                    )
                )
            } ?: listOf()
        ),
        stats = MediaStats(
            scoreDistribution = media?.stats?.scoreDistribution?.map {
                ScoreDistribution(
                    score = it?.score ?: 0,
                    amount = it?.amount ?: 0
                )
            } ?: listOf(),
            statusDistribution = media?.stats?.statusDistribution?.map {
                StatusDistribution(
                    status = it?.status,
                    amount = it?.amount ?: 0
                )
            } ?: listOf()
        ),
        siteUrl = media?.siteUrl ?: ""
    )
}