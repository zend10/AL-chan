package com.zen.alchan.data.converter

import com.zen.alchan.SearchUserQuery
import com.zen.alchan.data.response.anilist.*

fun SearchUserQuery.Data.convert(): Page<User> {
    return Page(
        pageInfo = PageInfo(
            total = Page?.pageInfo?.total ?: 0,
            perPage = Page?.pageInfo?.perPage ?: 0,
            currentPage = Page?.pageInfo?.currentPage ?: 0,
            lastPage = Page?.pageInfo?.lastPage ?: 0,
            hasNextPage = Page?.pageInfo?.hasNextPage ?: false
        ),
        data = Page?.users?.filterNotNull()?.map {
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