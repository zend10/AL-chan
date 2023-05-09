package com.zen.alchan.data.converter

import com.zen.alchan.MediaQuery
import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.*

fun MediaQuery.Data.convert(): Media {
    return Media(
        idAniList = Media?.id ?: 0,
        idMal = Media?.idMal,
        title = MediaTitle(
            romaji = Media?.title?.romaji ?: "",
            english = Media?.title?.english ?: "",
            native = Media?.title?.native ?: "",
            userPreferred = Media?.title?.userPreferred ?: ""
        ),
        type = Media?.type,
        format = Media?.format,
        status = Media?.status,
        description = Media?.description ?: "",
        startDate = if (Media?.startDate != null)
            FuzzyDate(year = Media.startDate.year, month = Media.startDate.month, day = Media.startDate.day)
        else
            null,
        endDate = if (Media?.endDate != null)
            FuzzyDate(year = Media.endDate.year, month = Media.endDate.month, day = Media.endDate.day)
        else
            null,
        season = Media?.season,
        seasonYear = Media?.seasonYear,
        episodes = Media?.episodes,
        duration = Media?.duration,
        chapters = Media?.chapters,
        volumes = Media?.volumes,
        countryOfOrigin = Media?.countryOfOrigin,
        isLicensed = Media?.isLicensed,
        source = Media?.source,
        trailer = if (Media?.trailer != null)
            MediaTrailer(
                id = Media.trailer.id ?: "",
                site = Media.trailer.site ?: "",
                thumbnail = Media.trailer.thumbnail ?: ""
            )
        else
            null,
        coverImage = MediaCoverImage(
            extraLarge = Media?.coverImage?.extraLarge ?: "",
            large = Media?.coverImage?.large ?: "",
            medium = Media?.coverImage?.medium ?: ""
        ),
        bannerImage = Media?.bannerImage ?: "",
        genres = Media?.genres?.filterNotNull()?.map { Genre(name = it) } ?: listOf(),
        synonyms = Media?.synonyms?.filterNotNull() ?: listOf(),
        averageScore = Media?.averageScore ?: 0,
        meanScore = Media?.meanScore ?: 0,
        popularity = Media?.popularity ?: 0,
        trending = Media?.trending ?: 0,
        favourites = Media?.favourites ?: 0,
        tags = Media?.tags?.filterNotNull()?.map {
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
            edges = Media?.relations?.edges?.map {
                MediaEdge(
                    node = Media(
                        idAniList = it?.node?.id ?: 0,
                        title = MediaTitle(
                            romaji = it?.node?.title?.romaji ?: "",
                            english = it?.node?.title?.english ?: "",
                            native = it?.node?.title?.native ?: "",
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
            nodes = Media?.characters?.nodes?.filterNotNull()?.map {
                Character(
                    id = it.id,
                    name = CharacterName(
                        first = it.name?.first ?: "",
                        middle = it.name?.middle ?: "",
                        last = it.name?.last ?: "",
                        full = it.name?.full ?: "",
                        native = it.name?.native ?: "",
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
            edges = Media?.staff?.edges?.map {
                StaffEdge(
                    node = Staff(
                        id = it?.node?.id ?: 0,
                        name = StaffName(
                            first = it?.node?.name?.first ?: "",
                            middle = it?.node?.name?.middle ?: "",
                            last = it?.node?.name?.last ?: "",
                            full = it?.node?.name?.full ?: "",
                            native = it?.node?.name?.native ?: "",
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
            edges = Media?.studios?.edges?.map {
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
        isFavourite = Media?.isFavourite ?: false,
        isAdult = Media?.isAdult ?: false,
        nextAiringEpisode = if (Media?.nextAiringEpisode != null)
            AiringSchedule(
                id = Media.nextAiringEpisode.id,
                airingAt = Media.nextAiringEpisode.airingAt,
                timeUntilAiring = Media.nextAiringEpisode.timeUntilAiring,
                episode = Media.nextAiringEpisode.episode
            )
        else
            null
        ,
        externalLinks = listOf(MediaExternalLink(
            url = Media?.siteUrl ?: "",
            site = "AniList",
            color = "#324760"
        )) + (Media?.externalLinks?.filterNotNull()?.map {
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
        } ?: listOf()),
        rankings = Media?.rankings?.map {
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
            nodes = Media?.recommendations?.nodes?.map {
                Recommendation(
                    id = it?.id ?: 0,
                    rating = it?.rating ?: 0,
                    userRating = it?.userRating,
                    mediaRecommendation = Media(
                        idAniList = it?.mediaRecommendation?.id ?: 0,
                        title = MediaTitle(
                            romaji = it?.mediaRecommendation?.title?.romaji ?: "",
                            english = it?.mediaRecommendation?.title?.english ?: "",
                            native = it?.mediaRecommendation?.title?.native ?: "",
                            userPreferred = it?.mediaRecommendation?.title?.userPreferred ?: ""
                        ),
                        type = it?.mediaRecommendation?.type,
                        format = it?.mediaRecommendation?.format,
                        status = it?.mediaRecommendation?.status,
                        startDate = if (it?.mediaRecommendation?.startDate != null)
                            FuzzyDate(year = it.mediaRecommendation.startDate.year, month = it.mediaRecommendation.startDate.month, day = it.mediaRecommendation.startDate.day)
                        else
                            null,
                        episodes = it?.mediaRecommendation?.episodes,
                        chapters = it?.mediaRecommendation?.chapters,
                        coverImage = MediaCoverImage(
                            extraLarge = it?.mediaRecommendation?.coverImage?.extraLarge ?: "",
                            large = it?.mediaRecommendation?.coverImage?.large ?: "",
                            medium = it?.mediaRecommendation?.coverImage?.medium ?: ""
                        ),
                        averageScore = it?.mediaRecommendation?.averageScore ?: 0,
                        favourites = it?.mediaRecommendation?.favourites ?: 0
                    )
                )
            } ?: listOf()
        ),
        stats = MediaStats(
            scoreDistribution = Media?.stats?.scoreDistribution?.map {
                ScoreDistribution(
                    score = it?.score ?: 0,
                    amount = it?.amount ?: 0
                )
            } ?: listOf(),
            statusDistribution = Media?.stats?.statusDistribution?.map {
                StatusDistribution(
                    status = it?.status,
                    amount = it?.amount ?: 0
                )
            } ?: listOf()
        ),
        siteUrl = Media?.siteUrl ?: ""
    )
}