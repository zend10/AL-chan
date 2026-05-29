package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.FuzzyDate
import kotlinx.serialization.Serializable

@Serializable
data class FuzzyDateResponse(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null
) {
    fun toModel(): FuzzyDate {
        return FuzzyDate(
            year = year ?: 0,
            month = month ?: 0,
            day = day ?: 0
        )
    }
}
