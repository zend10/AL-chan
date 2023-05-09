package com.zen.alchan.data.converter

import com.zen.alchan.SearchStaffQuery
import com.zen.alchan.data.response.anilist.*

fun SearchStaffQuery.Data.convert(): Page<Staff> {
    return Page(
        pageInfo = PageInfo(
            total = Page?.pageInfo?.total ?: 0,
            perPage = Page?.pageInfo?.perPage ?: 0,
            currentPage = Page?.pageInfo?.currentPage ?: 0,
            lastPage = Page?.pageInfo?.lastPage ?: 0,
            hasNextPage = Page?.pageInfo?.hasNextPage ?: false
        ),
        data = Page?.staff?.filterNotNull()?.map {
            Staff(
                id = it.id,
                name = StaffName(
                    first = it.name?.first ?: "",
                    middle = it.name?.middle ?: "",
                    last = it.name?.last ?: "",
                    full = it.name?.full ?: "",
                    native = it.name?.native ?: "",
                    alternative = it.name?.alternative?.filterNotNull() ?: listOf(),
                    userPreferred = it.name?.userPreferred ?: ""
                ),
                image = StaffImage(
                    large = it.image?.large ?: "",
                    medium = it.image?.medium ?: ""
                ),
                favourites = it.favourites ?: 0
            )
        } ?: listOf()
    )
}