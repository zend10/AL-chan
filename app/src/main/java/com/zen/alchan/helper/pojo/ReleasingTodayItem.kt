package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.MediaList

data class ReleasingTodayItem(
    val mediaList: MediaList,
    val episode: Int,
    val timeUntilAiring: Int
)