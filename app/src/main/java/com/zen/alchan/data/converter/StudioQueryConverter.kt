package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

fun StudioQuery.Data.convert(): Studio {
    return studio?.let {
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
                                native = it?.node?.title?.native_ ?: "",
                                userPreferred = it?.node?.title?.userPreferred ?: ""
                            ),
                            type = it?.node?.type,
                            format = it?.node?.format
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