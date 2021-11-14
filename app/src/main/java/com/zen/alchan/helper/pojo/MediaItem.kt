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
    }
}