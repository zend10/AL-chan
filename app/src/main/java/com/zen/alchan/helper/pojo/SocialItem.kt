package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Activity

data class SocialItem(
    val activity: Activity? = null,
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_HEADER = 100
        const val VIEW_TYPE_FRIENDS_ACTIVITY_HEADER = 200
        const val VIEW_TYPE_FRIENDS_ACTIVITY = 201
        const val VIEW_TYPE_FRIENDS_ACTIVITY_SEE_MORE = 202
        const val VIEW_TYPE_GLOBAL_ACTIVITY_HEADER = 300
        const val VIEW_TYPE_GLOBAL_ACTIVITY = 301
        const val VIEW_TYPE_GLOBAL_ACTIVITY_SEE_MORE = 302
    }
}