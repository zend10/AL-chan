package com.zen.alchan.data.response.anilist

data class Studio(
    val id: Int = 0,
    val name: String = "",
    val isAnimationStudio: Boolean = false,
    val media: MediaConnection = MediaConnection(),
    val siteUrl: String = "",
    val isFavourite: Boolean = false,
    val favourites: Int = 0
)