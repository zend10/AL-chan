package com.zen.alchan.ui.browse.activity

import com.zen.alchan.helper.pojo.BestFriend
import type.ActivityType
import type.MediaType

interface ActivityListener {
    fun openActivityPage(activityId: Int)
    fun openUserPage(userId: Int)
    fun toggleLike(activityId: Int)
    fun toggleSubscribe(activityId: Int, subscribe: Boolean)
    fun editActivity(activityId: Int, text: String, recipientId: Int?, recipientName: String?)
    fun deleteActivity(activityId: Int)
    fun viewOnAniList(siteUrl: String?)
    fun copyLink(siteUrl: String?)
    fun openMediaPage(mediaId: Int, mediaType: MediaType?)
    fun changeActivityType(selectedActivityType: ArrayList<ActivityType>?)
    fun changeBestFriend(selectedBestFriendPosition: Int)
}