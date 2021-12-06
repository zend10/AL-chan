package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.PageInfo
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.data.response.anilist.UserAvatar

fun FollowingQuery.Data.convert(): Pair<PageInfo, List<User>> {
    return Pair(
        PageInfo(
            total = page?.pageInfo?.total ?: 0,
            perPage = page?.pageInfo?.perPage ?: 0,
            currentPage = page?.pageInfo?.currentPage ?: 0,
            lastPage = page?.pageInfo?.lastPage ?: 0,
            hasNextPage = page?.pageInfo?.hasNextPage ?: false
        ),
        page?.following?.filterNotNull()?.map {
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