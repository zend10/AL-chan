package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

fun SearchStudioQuery.Data.convert(): Page<Studio> {
    return Page(
        pageInfo = PageInfo(
            total = page?.pageInfo?.total ?: 0,
            perPage = page?.pageInfo?.perPage ?: 0,
            currentPage = page?.pageInfo?.currentPage ?: 0,
            lastPage = page?.pageInfo?.lastPage ?: 0,
            hasNextPage = page?.pageInfo?.hasNextPage ?: false
        ),
        data = page?.studios?.filterNotNull()?.map {
            Studio(
                id = it.id,
                name = it.name,
                favourites = it.favourites ?: 0,
                media = MediaConnection(
                    nodes = it.media?.nodes?.filterNotNull()?.map {
                        Media(
                            idAniList = it.id,
                            title = MediaTitle(
                                romaji = it.title?.romaji ?: "",
                                english = it.title?.english ?: "",
                                native = it.title?.native_ ?: "",
                                userPreferred = it.title?.userPreferred ?: ""
                            ),
                            coverImage = MediaCoverImage(
                                extraLarge = it.coverImage?.extraLarge ?: "",
                                large = it.coverImage?.large ?: "",
                                medium = it.coverImage?.medium ?: ""
                            )
                        )
                    } ?: listOf()
                )
            )
        } ?: listOf()
    )
}