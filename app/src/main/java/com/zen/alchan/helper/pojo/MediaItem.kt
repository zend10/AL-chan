package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media

data class MediaItem(
    val media: Media = Media(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_SYNOPSIS = 100
        const val VIEW_TYPE_CHARACTERS = 200
        const val VIEW_TYPE_INFO = 300
        const val VIEW_TYPE_GENRE = 400
        const val VIEW_TYPE_TAGS = 500
        const val VIEW_TYPE_THEMES = 600
        const val VIEW_TYPE_STATS = 700
        const val VIEW_TYPE_RELATIONS = 800
        const val VIEW_TYPE_RECOMMENDATIONS = 900
        const val VIEW_TYPE_TRAILERS = 1000
        const val VIEW_TYPE_LINKS = 1100
        const val VIEW_TYPE_STAFF = 1200
    }
}