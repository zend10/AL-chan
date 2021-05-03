package com.zen.alchan.data.response.anilist

data class MediaTag(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val rank: Int = 0,
    val isGeneralSpoiler: Boolean = false,
    val isMediaSpoiler: Boolean = false,
    val isAdult: Boolean = false
)