package com.zen.alchan.data.model.api

import kotlinx.serialization.Serializable

@Serializable
data class FuzzyDate(
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0
)
