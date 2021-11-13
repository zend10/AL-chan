package com.zen.alchan.helper.pojo

data class MediaItem(
    val synopsis: String? = null,
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_SYNOPSIS = 100
    }
}