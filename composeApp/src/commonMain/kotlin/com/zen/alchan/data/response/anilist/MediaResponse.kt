package com.zen.alchan.data.response.anilist

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
)
