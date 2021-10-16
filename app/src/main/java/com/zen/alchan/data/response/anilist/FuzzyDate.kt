package com.zen.alchan.data.response.anilist

data class FuzzyDate(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null
) {
    fun isNull(): Boolean {
        return year == null || month == null || day == null
    }
}