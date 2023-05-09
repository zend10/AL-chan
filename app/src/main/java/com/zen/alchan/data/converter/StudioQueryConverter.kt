package com.zen.alchan.data.converter

import com.zen.alchan.StudioQuery
import com.zen.alchan.data.response.anilist.*

fun StudioQuery.Data.convert(): Studio {
    return Studio?.let {
        Studio(
            id = it.id,
            name = it.name,
            isAnimationStudio = it.isAnimationStudio,
            media = MediaConnection(
                edges = it.media?.edges?.map {
                    MediaEdge(
                        node = Media(
                            idAniList = it?.node?.id ?: 0,
                            title = MediaTitle(
                                romaji = it?.node?.title?.romaji ?: "",
                                english = it?.node?.title?.english ?: "",
                                native = it?.node?.title?.native ?: "",
                                userPreferred = it?.node?.title?.userPreferred ?: ""
                            ),
                            type = it?.node?.type,
                            format = it?.node?.format,
                            coverImage = MediaCoverImage(
                                extraLarge = it?.node?.coverImage?.extraLarge ?: "",
                                large = it?.node?.coverImage?.large ?: "",
                                medium = it?.node?.coverImage?.medium ?: ""
                            ),
                            averageScore = it?.node?.averageScore ?: 0,
                            meanScore = it?.node?.meanScore ?: 0,
                            popularity = it?.node?.popularity ?: 0,
                            favourites = it?.node?.favourites ?: 0,
                            startDate = if (it?.node?.startDate != null)
                                FuzzyDate(year = it?.node.startDate.year, month = it.node.startDate.month, day = it.node.startDate.day)
                            else
                                null
                        ),
                        isMainStudio = it?.isMainStudio ?: false
                    )
                } ?: listOf(),
                pageInfo = PageInfo(
                    total = it.media?.pageInfo?.total ?: 0,
                    perPage = it.media?.pageInfo?.perPage ?: 0,
                    currentPage = it.media?.pageInfo?.currentPage ?: 0,
                    lastPage = it.media?.pageInfo?.lastPage ?: 0,
                    hasNextPage = it.media?.pageInfo?.hasNextPage ?: false
                )
            ),
            siteUrl = it.siteUrl ?: "",
            isFavourite = it.isFavourite,
            favourites = it.favourites ?: 0
        )
    } ?: Studio()
}