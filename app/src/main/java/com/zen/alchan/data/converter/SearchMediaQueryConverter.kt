package com.zen.alchan.data.converter

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
                startDate = if (it.startDate != null)
                    FuzzyDate(
                        year = it.startDate.year,
                        month = it.startDate.month,
                        day = it.startDate.day
                    )
                else
                    null,
                coverImage = MediaCoverImage(
                    extraLarge = it.coverImage?.extraLarge ?: "",
                    large = it.coverImage?.large ?: "",
                    medium = it.coverImage?.medium ?: ""
                ),
                bannerImage = it.bannerImage ?: "",
                averageScore = it.averageScore ?: 0,
                meanScore = it.meanScore ?: 0,
                popularity = it.popularity ?: 0,
                favourites = it.favourites ?: 0,
                mediaListEntry = if (it.mediaListEntry != null) MediaList(status = it.mediaListEntry.status) else null
            )
        } ?: listOf()
    )
}