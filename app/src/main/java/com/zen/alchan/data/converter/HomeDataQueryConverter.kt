package com.zen.alchan.data.converter

import com.zen.alchan.data.response.*
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.data.response.Genre
import fragment.HomeMedia

fun HomeDataQuery.Data.convert(): HomeData {
    val trendingAnime = trendingAnime?.media?.mapNotNull { it?.fragments?.homeMedia?.convert() } ?: listOf()
    val trendingManga = trendingManga?.media?.mapNotNull { it?.fragments?.homeMedia?.convert() } ?: listOf()
    val newAnime = newAnime?.media?.mapNotNull { it?.fragments?.homeMedia?.convert() } ?: listOf()
    val newManga = newManga?.media?.mapNotNull { it?.fragments?.homeMedia?.convert() } ?: listOf()
    val review = review?.reviews?.mapNotNull {
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

private fun HomeMedia?.convert(): Media {
    return Media(
        idAniList = this?.id ?: 0,
        idMal = this?.idMal,
        title = MediaTitle(romaji = this?.title?.romaji ?: "", english = this?.title?.english ?: "", native = this?.title?.native_ ?: "", userPreferred = this?.title?.userPreferred ?: ""),
        type = this?.type,
        format = this?.format,
        status = this?.status,
        description = this?.description ?: "",
        startDate = if (this?.startDate?.year != null) {
            FuzzyDate(this.startDate.year, this.startDate.month, this.startDate.day)
        } else {
            null
        },
        coverImage = MediaCoverImage(
            this?.coverImage?.extraLarge ?: "",
            this?.coverImage?.large ?: "",
            this?.coverImage?.medium ?: ""
        ),
        bannerImage = this?.bannerImage ?: "",
        genres = this?.genres?.mapNotNull { Genre(name = it ?: "") } ?: listOf(),
        averageScore = this?.averageScore ?: 0,
        favourites = this?.favourites ?: 0,
        staff = this?.staff?.convert() ?: StaffConnection(),
        studios = this?.studios?.convert() ?: StudioConnection()
    )
}

private fun HomeMedia.Staff?.convert(): StaffConnection {
    return StaffConnection(
        edges = this?.edges?.map {
            StaffEdge(
                node = Staff(
                    id = it?.node?.id ?: 0,
                    name = StaffName(
                        full = it?.node?.name?.full ?: ""
                    )
                ),
                role = it?.role ?: ""
            )
        } ?: listOf()
    )
}

private fun HomeMedia.Studios?.convert(): StudioConnection {
    return StudioConnection(
        edges = this?.edges?.map {
            StudioEdge(
                node = Studio(
                    id = it?.node?.id ?: 0,
                    name = it?.node?.name ?: ""
                ),
                isMain = it?.isMain ?: false
            )
        } ?: listOf()
    )
}
