package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Media

data class MediaStatsItem(
    val media: Media = Media(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_STATS = 100
        const val VIEW_TYPE_FRIENDS_ACTIVITY_HEADER = 200
        const val VIEW_TYPE_FRIENDS_ACTIVITY = 201
        const val VIEW_TYPE_FRIENDS_ACTIVITY_SEE_MORE = 202
        const val VIEW_TYPE_REVIEW_HEADER = 300
        const val VIEW_TYPE_REVIEW = 301
        const val VIEW_TYPE_REVIEW_SEE_MORE = 302
    }
}