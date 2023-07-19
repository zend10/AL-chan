package com.zen.alchan.data.converter

import com.zen.alchan.SearchCharacterQuery
import com.zen.alchan.data.response.anilist.*

fun SearchCharacterQuery.Data.convert(): Page<Character> {
    return Page(
        pageInfo = PageInfo(
            total = Page?.pageInfo?.total ?: 0,
            perPage = Page?.pageInfo?.perPage ?: 0,
            currentPage = Page?.pageInfo?.currentPage ?: 0,
            lastPage = Page?.pageInfo?.lastPage ?: 0,
            hasNextPage = Page?.pageInfo?.hasNextPage ?: false
        ),
        data = Page?.characters?.filterNotNull()?.map {
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