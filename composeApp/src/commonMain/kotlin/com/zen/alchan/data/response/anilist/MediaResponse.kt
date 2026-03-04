package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class MediaResponse(
    val id: Int? = null,
    val idMal: Int? = null,
    val title: MediaTitleResponse? = null
)
