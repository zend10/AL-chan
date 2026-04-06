package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.MediaCoverImage
import kotlinx.serialization.Serializable

@Serializable
data class MediaCoverImageResponse(
    val color: String? = null,
    val extraLarge: String? = null,
    val large: String? = null,
    val medium: String? = null
) {
    fun toModel(): MediaCoverImage {
        return MediaCoverImage(
            color = color ?: "",
            extraLarge = extraLarge ?: "",
            large = large ?: "",
            medium = medium ?: ""
        )
    }
}
