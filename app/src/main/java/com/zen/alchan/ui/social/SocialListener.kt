package com.zen.alchan.ui.social

import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.User

interface SocialListener {
    fun navigateToUser(user: User)
    fun navigateToMedia(media: Media)
    fun navigateToActivity(activity: Activity)
    fun navigateToActivityList()
    fun toggleLike(activity: Activity)
    fun toggleSubscribe(activity: Activity)
    fun viewOnAniList(activity: Activity)
    fun copyActivityLink(activity: Activity)
    fun report(activity: Activity)
}