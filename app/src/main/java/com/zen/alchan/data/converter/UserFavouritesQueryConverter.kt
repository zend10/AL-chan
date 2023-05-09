package com.zen.alchan.data.converter

import com.zen.alchan.UserFavouritesQuery
import com.zen.alchan.data.response.anilist.*

fun UserFavouritesQuery.Data.convert(): Favourites {
    val favourites = User?.favourites ?: return Favourites()
    return Favourites(
        anime = MediaConnection(
            nodes = favourites.anime?.nodes?.map {
                Media(
                    idAniList = it?.id ?: 0,
                    title = MediaTitle(
                        romaji = it?.title?.romaji ?: "",
                        english = it?.title?.english ?: "",
                        native = it?.title?.native ?: "",
                        userPreferred = it?.title?.userPreferred ?: "",
                    ),
                    coverImage = MediaCoverImage(
                        extraLarge = it?.coverImage?.extraLarge ?: "",
                        large = it?.coverImage?.large ?: "",
                        medium = it?.coverImage?.medium ?: ""
                    ),
                    siteUrl = it?.siteUrl ?: ""
                )
            } ?: listOf(),
            pageInfo = favourites.anime?.pageInfo?.let {
                PageInfo(
                    total = it.total ?: 0,
                    perPage = it.perPage ?: 0,
                    currentPage = it.currentPage ?: 0,
                    lastPage = it.lastPage ?: 0,
                    hasNextPage = it.hasNextPage ?: false
                )
            } ?: PageInfo()
        ),
        manga = MediaConnection(
            nodes = favourites.manga?.nodes?.map {
                Media(
                    idAniList = it?.id ?: 0,
                    title = MediaTitle(
                        romaji = it?.title?.romaji ?: "",
                        english = it?.title?.english ?: "",
                        native = it?.title?.native ?: "",
                        userPreferred = it?.title?.userPreferred ?: "",
                    ),
                    coverImage = MediaCoverImage(
                        extraLarge = it?.coverImage?.extraLarge ?: "",
                        large = it?.coverImage?.large ?: "",
                        medium = it?.coverImage?.medium ?: ""
                    ),
                    siteUrl = it?.siteUrl ?: ""
                )
            } ?: listOf(),
            pageInfo = favourites.manga?.pageInfo?.let {
                PageInfo(
                    total = it.total ?: 0,
                    perPage = it.perPage ?: 0,
                    currentPage = it.currentPage ?: 0,
                    lastPage = it.lastPage ?: 0,
                    hasNextPage = it.hasNextPage ?: false
                )
            } ?: PageInfo()
        ),
        characters = CharacterConnection(
            nodes = favourites.characters?.nodes?.map {
                Character(
                    id = it?.id ?: 0,
                    name = CharacterName(
                        first = it?.name?.first ?: "",
                        middle = it?.name?.middle ?: "",
                        last = it?.name?.last ?: "",
                        full = it?.name?.full ?: "",
                        native = it?.name?.native ?: "",
                        alternative = it?.name?.alternative?.filterNotNull() ?: listOf(),
                        alternativeSpoiler = it?.name?.alternativeSpoiler?.filterNotNull() ?: listOf(),
                        userPreferred = it?.name?.first ?: "",
                    ),
                    image = CharacterImage(
                        large = it?.image?.large ?: "",
                        medium = it?.image?.medium ?: ""
                    ),
                    siteUrl = it?.siteUrl ?: ""
                )
            } ?: listOf(),
            pageInfo = favourites.characters?.pageInfo?.let {
                PageInfo(
                    total = it.total ?: 0,
                    perPage = it.perPage ?: 0,
                    currentPage = it.currentPage ?: 0,
                    lastPage = it.lastPage ?: 0,
                    hasNextPage = it.hasNextPage ?: false
                )
            } ?: PageInfo()
        ),
        staff = StaffConnection(
            nodes = favourites.staff?.nodes?.map {
                Staff(
                    id = it?.id ?: 0,
                    name = StaffName(
                        first = it?.name?.first ?: "",
                        middle = it?.name?.middle ?: "",
                        last = it?.name?.last ?: "",
                        full = it?.name?.full ?: "",
                        native = it?.name?.native ?: "",
                        alternative = it?.name?.alternative?.filterNotNull() ?: listOf(),
                        userPreferred = it?.name?.userPreferred ?: ""
                    ),
                    language = it?.languageV2 ?: "",
                    image = StaffImage(
                        large = it?.image?.large ?: "",
                        medium = it?.image?.medium ?: ""
                    ),
                    siteUrl = it?.siteUrl ?: ""
                )
            } ?: listOf(),
            pageInfo = favourites.staff?.pageInfo?.let {
                PageInfo(
                    total = it.total ?: 0,
                    perPage = it.perPage ?: 0,
                    currentPage = it.currentPage ?: 0,
                    lastPage = it.lastPage ?: 0,
                    hasNextPage = it.hasNextPage ?: false
                )
            } ?: PageInfo()
        ),
        studios = StudioConnection(
            nodes = favourites.studios?.nodes?.map {
                Studio(
                    id = it?.id ?: 0,
                    name = it?.name ?: "",
                    siteUrl = it?.siteUrl ?: ""
                )
            } ?: listOf(),
            pageInfo = favourites.studios?.pageInfo?.let {
                PageInfo(
                    total = it.total ?: 0,
                    perPage = it.perPage ?: 0,
                    currentPage = it.currentPage ?: 0,
                    lastPage = it.lastPage ?: 0,
                    hasNextPage = it.hasNextPage ?: false
                )
            } ?: PageInfo()
        )
    )
}