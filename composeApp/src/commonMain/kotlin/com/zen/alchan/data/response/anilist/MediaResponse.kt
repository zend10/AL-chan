package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.Media
import com.zen.alchan.data.model.api.MediaCoverImage
import com.zen.alchan.data.model.api.MediaTitle
import kotlinx.serialization.Serializable

@Serializable
data class MediaResponse(
    val id: Int? = null,
    val idMal: Int? = null,
    val title: MediaTitleResponse? = null,
    val bannerImage: String? = null,
    val coverImage: MediaCoverImageResponse? = null,
    val episodes: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val nextAiringEpisode: AiringScheduleResponse? = null
) {
    fun toModel(): Media {
        return Media(
            id = id?.toString() ?: "",
            idMal = idMal?.toString() ?: "",
            title = title?.toModel() ?: MediaTitle(),
            bannerImage = bannerImage ?: "",
            coverImage = coverImage?.toModel() ?: MediaCoverImage(),
            episodes = episodes,
            chapters = chapters,
            volumes = volumes,
            nextAiringEpisode = nextAiringEpisode?.toModel()
        )
    }
}
