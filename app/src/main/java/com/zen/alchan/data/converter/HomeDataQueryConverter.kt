package com.zen.alchan.data.converter

import com.zen.alchan.HomeDataQuery
import com.zen.alchan.data.response.*
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.data.response.Genre
import com.zen.alchan.fragment.HomeMedia

fun HomeDataQuery.Data.convert(): HomeData {
    val trendingAnime = trendingAnime?.media?.mapNotNull { it?.homeMedia?.convert() } ?: listOf()
    val trendingManga = trendingManga?.media?.mapNotNull { it?.homeMedia?.convert() } ?: listOf()
    val newAnime = newAnime?.media?.mapNotNull { it?.homeMedia?.convert() } ?: listOf()
    val newManga = newManga?.media?.mapNotNull { it?.homeMedia?.convert() } ?: listOf()

    return HomeData(
        trendingAnime = trendingAnime,
        trendingManga = trendingManga,
        newAnime = newAnime,
        newManga = newManga
    )
}

private fun HomeMedia?.convert(): Media {
    return Media(
        idAniList = this?.id ?: 0,
        idMal = this?.idMal,
        title = MediaTitle(romaji = this?.title?.romaji ?: "", english = this?.title?.english ?: "", native = this?.title?.native ?: "", userPreferred = this?.title?.userPreferred ?: ""),
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
        countryOfOrigin = this?.countryOfOrigin,
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
