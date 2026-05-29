package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class MediaTitle(
    val romaji: String = "",
    val english: String = "",
    val native: String = "",
    val userPreferred: String = ""
)