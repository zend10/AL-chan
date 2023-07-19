package com.zen.alchan.data.converter

import com.zen.alchan.FollowingQuery
import com.zen.alchan.data.response.anilist.Page
import com.zen.alchan.data.response.anilist.PageInfo
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.anilist.UserAvatar

fun FollowingQuery.Data.convert(): Page<User> {
    return Page(
        PageInfo(
            total = Page?.pageInfo?.total ?: 0,
            perPage = Page?.pageInfo?.perPage ?: 0,
            currentPage = Page?.pageInfo?.currentPage ?: 0,
            lastPage = Page?.pageInfo?.lastPage ?: 0,
            hasNextPage = Page?.pageInfo?.hasNextPage ?: false
        ),
        Page?.following?.filterNotNull()?.map {
            User(
                id = it.id,
                name = it.name,
                avatar = UserAvatar(
                    large = it.avatar?.large ?: "",
                    medium = it.avatar?.medium ?: ""
                ),
                bannerImage = it.bannerImage ?: "",
                isFollowing = it.isFollowing ?: false,
                isFollower = it.isFollower ?: false,
                siteUrl = it.siteUrl ?: ""
            )
        } ?: listOf()
    )
}