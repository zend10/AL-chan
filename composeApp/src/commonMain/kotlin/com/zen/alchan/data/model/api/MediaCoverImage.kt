package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class MediaCoverImage(
    val color: String = "",
    val extraLarge: String = "",
    val large: String = "",
    val medium: String = ""
)
