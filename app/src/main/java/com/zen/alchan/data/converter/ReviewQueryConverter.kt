package com.zen.alchan.data.converter

import com.zen.alchan.ReviewQuery
import com.zen.alchan.data.response.anilist.*

fun ReviewQuery.Data.convert(): Page<Review> {
    return Page(
        pageInfo = PageInfo(
            total = Page?.pageInfo?.total ?: 0,
            perPage = Page?.pageInfo?.perPage ?: 0,
            currentPage = Page?.pageInfo?.currentPage ?: 0,
            lastPage = Page?.pageInfo?.lastPage ?: 0,
            hasNextPage = Page?.pageInfo?.hasNextPage ?: false
        ),
        data = Page?.reviews?.map {
            Review(
                id = it?.id ?: 0,
                userId = it?.userId ?: 0,
                mediaId = it?.mediaId ?: 0,
                mediaType = it?.mediaType,
                summary = it?.summary ?: "",
                body = it?.body ?: "",
                rating = it?.rating ?: 0,
                ratingAmount = it?.ratingAmount ?: 0,
                userRating = it?.userRating,
                score = it?.score ?: 0,
                private = it?.private ?: false,
                siteUrl = it?.siteUrl ?: "",
                createdAt = it?.createdAt ?: 0,
                updatedAt = it?.updatedAt ?: 0,
                user = User(
                    id = it?.user?.id ?: 0,
                    name = it?.user?.name ?: "",
                    avatar = UserAvatar(
                        large = it?.user?.avatar?.large ?: "",
                        medium = it?.user?.avatar?.medium ?: ""
                    )
                ),
                media = Media(
                    idAniList = it?.media?.id ?: 0,
                    title = MediaTitle(
                        romaji = it?.media?.title?.romaji ?: "",
                        english = it?.media?.title?.english ?: "",
                        native = it?.media?.title?.native ?: "",
                        userPreferred = it?.media?.title?.userPreferred ?: ""
                    ),
                    bannerImage = it?.media?.bannerImage ?: "",
                    format = it?.media?.format
                )
            )
        } ?: listOf()
    )
}