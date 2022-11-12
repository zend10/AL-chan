package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.ActivityReply

data class SocialItem(
    val activity: Activity? = null,
    val activityReply: ActivityReply? = null,
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_HEADER = 100
        const val VIEW_TYPE_FRIENDS_ACTIVITY_HEADER = 200
        const val VIEW_TYPE_FRIENDS_ACTIVITY_SEE_MORE = 202
        const val VIEW_TYPE_GLOBAL_ACTIVITY_HEADER = 300
        const val VIEW_TYPE_GLOBAL_ACTIVITY_SEE_MORE = 302
        const val VIEW_TYPE_ACTIVITY = 400
        const val VIEW_TYPE_ACTIVITY_REPLY = 500
    }
}