package com.zen.alchan.data.converter

import com.zen.alchan.data.model.*
import fragment.HomeMedia

fun HomeDataQuery.Data.convert(): HomeData {
    val trendingAnime = trendingAnime?.media?.map { convertHomeMediaFragment(it?.fragments?.homeMedia) } ?: listOf()
    val trendingManga = trendingManga?.media?.map { convertHomeMediaFragment(it?.fragments?.homeMedia) } ?: listOf()
    val newAnime = newAnime?.media?.map { convertHomeMediaFragment(it?.fragments?.homeMedia) } ?: listOf()
    val newManga = newManga?.media?.map { convertHomeMediaFragment(it?.fragments?.homeMedia) } ?: listOf()
    val review = review?.reviews?.map {
        Review(
            id = it?.id ?: 0,
            userId = it?.userId ?: 0,
            mediaId = it?.mediaId ?: 0,
            mediaType = it?.mediaType,
            summary = it?.summary ?: "",
            rating = it?.rating ?: 0,
            ratingAmount = it?.ratingAmount ?: 0,
            score = it?.score ?: 0,
            user = User(
                id = it?.user?.id ?: 0,
                name = it?.user?.name ?: "",
                avatar = UserAvatar(
                    it?.user?.avatar?.large ?: "",
                    it?.user?.avatar?.medium ?: ""
                )
            ),
            media = Media(
                idAniList = it?.media?.id ?: 0,
                title = MediaTitle(userPreferred = it?.media?.title?.userPreferred ?: ""),
                bannerImage = it?.media?.bannerImage ?: ""
            )
        )
    } ?: listOf()

    return HomeData(
        trendingAnime = trendingAnime,
        trendingManga = trendingManga,
        newAnime = newAnime,
        newManga = newManga,
        review = review
    )
}

private fun convertHomeMediaFragment(media: HomeMedia?): Media {
    return Media(
        idAniList = media?.id ?: 0,
        idMal = media?.idMal,
        title = MediaTitle(userPreferred = media?.title?.userPreferred ?: ""),
        type = media?.type,
        format = media?.format,
        status = media?.status,
        description = media?.description ?: "",
        startDate = if (media?.startDate?.year != null) {
            FuzzyDate(media.startDate.year, media.startDate.month, media.startDate.day)
        } else {
            null
        },
        coverImage = MediaCoverImage(
            media?.coverImage?.extraLarge ?: "",
            media?.coverImage?.large ?: "",
            media?.coverImage?.medium ?: ""
        ),
        bannerImage = media?.bannerImage ?: "",
        genres = media?.genres?.filterNotNull() ?: listOf(),
        averageScore = media?.averageScore ?: 0,
        meanScore = media?.favourites ?: 0
    )
}
