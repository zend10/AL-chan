package com.zen.alchan.data.response.anilist

data class StudioEdge(
    val node: Studio = Studio(),
    val id: Int = 0,
    val isMain: Boolean = false,
    val favouriteOrder: Int = 0
)