package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Review

data class HomeItem(
    val media: List<Media> = listOf(),
    val review: Review = Review(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_HEADER = 101
        const val VIEW_TYPE_MENU = 102
        const val VIEW_TYPE_RELEASING_TODAY = 200
        const val VIEW_TYPE_TRENDING_ANIME = 300
        const val VIEW_TYPE_TRENDING_MANGA = 301
        const val VIEW_TYPE_NEW_ANIME = 400
        const val VIEW_TYPE_NEW_MANGA = 401
        const val VIEW_TYPE_FIRST_REVIEW =  500
        const val VIEW_TYPE_REVIEW = 501

        val ITEM_HEADER = HomeItem(viewType = VIEW_TYPE_HEADER)
        val ITEM_MENU = HomeItem(viewType = VIEW_TYPE_MENU)
        val EMPTY_TRENDING_ANIME = HomeItem(viewType = VIEW_TYPE_TRENDING_ANIME)
        val EMPTY_TRENDING_MANGA = HomeItem(viewType = VIEW_TYPE_TRENDING_MANGA)
    }
}