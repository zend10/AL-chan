package com.zen.alchan.data.converter

import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.*

fun SearchMediaQuery.Data.convert(): Page<Media> {
    return Page(
        pageInfo = PageInfo(
            total = page?.pageInfo?.total ?: 0,
            perPage = page?.pageInfo?.perPage ?: 0,
            currentPage = page?.pageInfo?.currentPage ?: 0,
            lastPage = page?.pageInfo?.lastPage ?: 0,
            hasNextPage = page?.pageInfo?.hasNextPage ?: false
        ),
        data = page?.media?.filterNotNull()?.map {
            Media(
                idAniList = it.id,
                idMal = it.idMal,
                title = MediaTitle(
                    romaji = it.title?.romaji ?: "",
                    english = it.title?.english ?: "",
                    native = it.title?.native_ ?: "",
                    userPreferred = it.title?.userPreferred ?: ""
                ),
                type = it.type,
                format = it.format,
                status = it.status,
                description = it.description ?: "",
                episodes = it.episodes,
                chapters = it.chapters,
                volumes = it.volumes,
                startDate = if (it.startDate != null)
                    FuzzyDate(
                        year = it.startDate.year,
                        month = it.startDate.month,
                        day = it.startDate.day
                    )
                else
                    null,
                genres = it.genres?.filterNotNull()?.map { genre -> Genre(genre) } ?: listOf(),
                studios = StudioConnection(
                    edges = it.studios?.edges?.filterNotNull()?.map {
                        StudioEdge(
                            node = Studio(name = it.node?.name ?: "")
                        )
                    } ?: listOf()
                ),
                staff = StaffConnection(
                    edges = it.staff?.edges?.filterNotNull()?.map {
                        StaffEdge(
                            node = Staff(
                                name = StaffName(
                                    first = it.node?.name?.first ?: "",
                                    middle = it.node?.name?.middle ?: "",
                                    last = it.node?.name?.last ?: "",
                                    full = it.node?.name?.full ?: "",
                                    native = it.node?.name?.native_ ?: "",
                                    alternative = it.node?.name?.alternative?.filterNotNull() ?: listOf(),
                                    userPreferred = it.node?.name?.userPreferred ?: ""
                                )
                            ),
                            role = it.role ?: ""
                        )
                    } ?: listOf(),
                ),
                source = it.source,
                coverImage = MediaCoverImage(
                    extraLarge = it.coverImage?.extraLarge ?: "",
                    large = it.coverImage?.large ?: "",
                    medium = it.coverImage?.medium ?: ""
                ),
                bannerImage = it.bannerImage ?: "",
                averageScore = it.averageScore ?: 0,
                meanScore = it.meanScore ?: 0,
                popularity = it.popularity ?: 0,
                trending = it.trending ?: 0,
                favourites = it.favourites ?: 0,
                mediaListEntry = if (it.mediaListEntry != null) MediaList(status = it.mediaListEntry.status) else null,
                stats = MediaStats(
                    scoreDistribution = it.stats?.scoreDistribution?.map {
                        ScoreDistribution(
                            score = it?.score ?: 0,
                            amount = it?.amount ?: 0
                        )
                    } ?: listOf(),
                    statusDistribution = it.stats?.statusDistribution?.map {
                        StatusDistribution(
                            status = it?.status,
                            amount = it?.amount ?: 0
                        )
                    } ?: listOf()
                )
            )
        } ?: listOf()
    )
}