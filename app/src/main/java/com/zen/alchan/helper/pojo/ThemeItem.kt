package com.zen.alchan.helper.pojo

data class ThemeItem(
    val url: String = "",
    val searchQuery: String = "",
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_VIDEO = 100
        const val VIEW_TYPE_AUDIO = 200
        const val VIEW_TYPE_YOUTUBE = 300
        const val VIEW_TYPE_SPOTIFY = 400
        const val VIEW_TYPE_TEXT = 500
    }
}