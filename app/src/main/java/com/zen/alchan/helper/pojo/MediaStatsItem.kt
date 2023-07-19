package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaRank

data class MediaStatsItem(
    val media: Media = Media(),
    val mediaRank: MediaRank = MediaRank(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_STATS_HEADER = 100
        const val VIEW_TYPE_STATS = 101
//        const val VIEW_TYPE_FRIENDS_ACTIVITY_HEADER = 200
//        const val VIEW_TYPE_FRIENDS_ACTIVITY = 201
//        const val VIEW_TYPE_FRIENDS_ACTIVITY_SEE_MORE = 202
//        const val VIEW_TYPE_REVIEW_HEADER = 300
//        const val VIEW_TYPE_REVIEW = 301
//        const val VIEW_TYPE_REVIEW_SEE_MORE = 302
//        const val VIEW_TYPE_RECENT_ACTIVITY_HEADER = 400
//        const val VIEW_TYPE_RECENT_ACTIVITY = 401
//        const val VIEW_TYPE_RECENT_ACTIVITY_SEE_MORE = 402
        const val VIEW_TYPE_RANKING_HEADER = 500
        const val VIEW_TYPE_RANKING = 501
        const val VIEW_TYPE_STATUS_DISTRIBUTION_HEADER = 600
        const val VIEW_TYPE_STATUS_DISTRIBUTION = 601
        const val VIEW_TYPE_SCORE_DISTRIBUTION_HEADER = 700
        const val VIEW_TYPE_SCORE_DISTRIBUTION = 701
    }
}