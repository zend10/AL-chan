package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.enums.getCountryEnum
import com.zen.alchan.data.enums.getMediaFormatEnum
import com.zen.alchan.data.enums.getMediaStatusEnum
import com.zen.alchan.data.enums.getMediaTypeEnum
import com.zen.alchan.data.model.api.Media
import com.zen.alchan.data.model.api.MediaCoverImage
import com.zen.alchan.data.model.api.MediaTitle
import kotlinx.serialization.Serializable

@Serializable
data class MediaResponse(
    val id: Int? = null,
    val idMal: Int? = null,
    val title: MediaTitleResponse? = null,
    val countryOfOrigin: String? = null,
    val type: String? = null,
    val status: String? = null,
    val format: String? = null,
    val description: String? = null,
    val coverImage: MediaCoverImageResponse? = null,
    val bannerImage: String? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val episodes: Int? = null,
    val genres: List<String>? = null,
    val averageScore: Int? = null,
    val favourites: Int? = null,
    val staff: StaffConnectionResponse? = null,
    val studios: StudioConnectionResponse? = null,
    val startDate: FuzzyDateResponse? = null,
    val nextAiringEpisode: AiringScheduleResponse? = null
) {
    fun toModel(): Media {
        return Media(
            id = id?.toString() ?: "",
            idMal = idMal?.toString() ?: "",
            title = title?.toModel() ?: MediaTitle(),
            countryOfOrigin = getCountryEnum(countryOfOrigin),
            type = getMediaTypeEnum(type),
            status = getMediaStatusEnum(status),
            format = getMediaFormatEnum(format),
            description = description ?: "",
            coverImage = coverImage?.toModel() ?: MediaCoverImage(),
            bannerImage = bannerImage ?: "",
            chapters = chapters,
            volumes = volumes,
            episodes = episodes,
            genres = genres ?: listOf(),
            averageScore = averageScore ?: 0,
            favourites = favourites ?: 0,
            staff = staff?.toModel() ?: listOf(),
            studios = studios?.toModel() ?: listOf(),
            startDate = startDate?.toModel(),
            nextAiringEpisode = nextAiringEpisode?.toModel()
        )
    }
}
