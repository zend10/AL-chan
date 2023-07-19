package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Studio

class StudioItem(
    val studio: Studio = Studio(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_MEDIA = 100
    }
}