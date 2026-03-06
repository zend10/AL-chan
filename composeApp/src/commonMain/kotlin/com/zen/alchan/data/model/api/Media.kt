package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val id: Int = 0,
    val idMal: Int = 0,
    val title: MediaTitle = MediaTitle(),
    val bannerImage: String = "",
    val coverImage: MediaCoverImage = MediaCoverImage(),
    val episodes: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val nextAiringEpisode: AiringSchedule? = null
)