package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.Review

data class HomeItem(
    val media: List<Media> = listOf(),
    val releasingToday: List<ReleasingTodayItem> = listOf(),
    val review: Review = Review(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_HEADER = 100
        const val VIEW_TYPE_MENU = 101
        const val VIEW_TYPE_RELEASING_TODAY = 200
        const val VIEW_TYPE_TRENDING_ANIME = 300
        const val VIEW_TYPE_TRENDING_MANGA = 301
        const val VIEW_TYPE_NEW_ANIME = 400
        const val VIEW_TYPE_NEW_MANGA = 401
        const val VIEW_TYPE_FIRST_REVIEW =  500
        const val VIEW_TYPE_REVIEW = 501
        const val VIEW_TYPE_SOCIAL = 600
    }
}