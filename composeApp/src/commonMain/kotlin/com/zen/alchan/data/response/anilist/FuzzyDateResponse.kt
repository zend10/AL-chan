package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class FuzzyDateResponse(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null
)
