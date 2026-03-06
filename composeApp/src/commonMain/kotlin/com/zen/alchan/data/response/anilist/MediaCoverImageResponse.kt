package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class MediaCoverImageResponse(
    val color: String? = null,
    val extraLarge: String? = null,
    val large: String? = null,
    val medium: String? = null
)
