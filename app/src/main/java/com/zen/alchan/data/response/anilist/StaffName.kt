package com.zen.alchan.data.response.anilist

data class StaffName(
    val first: String = "",
    val middle: String = "",
    val last: String = "",
    val full: String = "",
    val native: String = "",
    val alternative: List<String> = listOf()
)