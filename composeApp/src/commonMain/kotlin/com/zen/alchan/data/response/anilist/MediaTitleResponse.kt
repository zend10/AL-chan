package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class MediaTitleResponse(
    val english: String? = null,
    val native: String? = null,
    val romaji: String? = null,
    val userPreferred: String? = null
)
