package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.*

fun SearchUserQuery.Data.convert(): Page<User> {
    return Page(
        pageInfo = PageInfo(
            total = page?.pageInfo?.total ?: 0,
            perPage = page?.pageInfo?.perPage ?: 0,
            currentPage = page?.pageInfo?.currentPage ?: 0,
            lastPage = page?.pageInfo?.lastPage ?: 0,
            hasNextPage = page?.pageInfo?.hasNextPage ?: false
        ),
        data = page?.users?.filterNotNull()?.map {
            User(
                id = it.id,
                name = it.name,
                avatar = UserAvatar(
                    large = it.avatar?.large ?: "",
                    medium = it.avatar?.medium ?: ""
                )
            )
        } ?: listOf()
    )
}