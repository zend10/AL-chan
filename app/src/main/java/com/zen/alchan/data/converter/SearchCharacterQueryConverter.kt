package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

fun SearchCharacterQuery.Data.convert(): Page<Character> {
    return Page(
        pageInfo = PageInfo(
            total = page?.pageInfo?.total ?: 0,
            perPage = page?.pageInfo?.perPage ?: 0,
            currentPage = page?.pageInfo?.currentPage ?: 0,
            lastPage = page?.pageInfo?.lastPage ?: 0,
            hasNextPage = page?.pageInfo?.hasNextPage ?: false
        ),
        data = page?.characters?.filterNotNull()?.map {
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
                    userPreferred = it.name?.userPreferred ?: ""
                ),
                image = CharacterImage(
                    large = it.image?.large ?: "",
                    medium = it.image?.medium ?: ""
                ),
                favourites = it.favourites ?: 0
            )
        } ?: listOf()
    )
}