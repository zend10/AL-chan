package com.zen.alchan.ui.browse.media.social

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import type.MediaType

class MediaSocialViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var mediaId: Int? = null
    var mediaType: MediaType? = null

    var friendsPage = 1
    var friendsHasNextPage = true
    var friendsIsInit = false
    var friendsMediaList = ArrayList<MediaSocialQuery.MediaList?>()

    var activityPage = 1
    var activityHasNextPage = true
    var activityIsInit = false
    var activityList = ArrayList<MediaActivityQuery.Activity?>()

    val mediaFriendsMediaListData by lazy {
        mediaRepository.mediaFriendsMediaListData
    }

    val mediaActivityData by lazy {
        mediaRepository.mediaActivityData
    }

    val triggerMediaSocial by lazy {
        mediaRepository.triggerMediaSocial
    }

    fun getMediaFriendsMediaList() {
        if (friendsHasNextPage && mediaId != null) {
            mediaRepository.getMediaFriendsMediaList(mediaId!!, friendsPage)
        }
    }

    fun getMediaActivity() {
        if (activityHasNextPage && mediaId != null) {
            mediaRepository.getMediaActivity(mediaId!!, activityPage)
        }
    }

    fun refresh() {
        friendsMediaList.clear()
        friendsPage = 1
        friendsHasNextPage = true
        getMediaFriendsMediaList()

        activityList.clear()
        activityPage = 1
        activityHasNextPage = true
        getMediaActivity()
    }
}