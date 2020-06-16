package com.zen.alchan.ui.social

import type.MediaType

interface ActivityListener {
    fun openActivityPage(activityId: Int)
    fun openUserPage(userId: Int)
    fun toggleLike(activityId: Int)
    fun toggleSubscribe(activityId: Int)
    fun editActivity(activityId: Int)
    fun deleteActivity(activityId: Int)
    fun viewOnAniList(siteUrl: String?)
    fun copyLink(siteUrl: String?)
    fun openMediaPage(mediaId: Int, mediaType: MediaType?)
}