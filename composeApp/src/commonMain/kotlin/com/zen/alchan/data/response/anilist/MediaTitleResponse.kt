package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.MediaTitle
import kotlinx.serialization.Serializable

@Serializable
data class MediaTitleResponse(
    val english: String? = null,
    val native: String? = null,
    val romaji: String? = null,
    val userPreferred: String? = null
) {
    fun toModel(): MediaTitle {
        return MediaTitle(
            english = english ?: "",
            native = native ?: "",
            romaji = romaji ?: "",
            userPreferred = userPreferred ?: ""
        )
    }
}
