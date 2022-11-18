package com.zen.alchan.ui.social

import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.ActivityReply
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.helper.enums.ActivityListPage

interface SocialListener {
    fun navigateToUser(user: User)
    fun navigateToMedia(media: Media)
    fun navigateToActivityDetail(activity: Activity)
    fun navigateToActivityList(activityListPage: ActivityListPage)
    fun toggleLike(activity: Activity, activityReply: ActivityReply? = null)
    fun viewLikes(activity: Activity, activityReply: ActivityReply? = null)
    fun toggleSubscribe(activity: Activity)
    fun viewOnAniList(activity: Activity)
    fun copyActivityLink(activity: Activity)
    fun report(activity: Activity)
    fun edit(activity: Activity, activityReply: ActivityReply? = null)
    fun delete(activity: Activity, activityReply: ActivityReply? = null)
    fun reply(activity: Activity, activityReply: ActivityReply? = null)
}