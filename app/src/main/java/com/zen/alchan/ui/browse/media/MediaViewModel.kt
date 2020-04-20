package com.zen.alchan.ui.browse.media

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.helper.enums.MediaPage
import type.MediaType

class MediaViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var mediaId: Int? = null
    var mediaType: MediaType? = null

    var currentMediaPage = MediaPage.OVERVIEW
    var currentMediaData: MediaQuery.Media? = null

    val mediaData by lazy {
        mediaRepository.mediaData
    }

    val mediaStatus by lazy {
        mediaRepository.mediaStatus
    }

    fun getMedia() {
        if (mediaId != null) mediaRepository.getMedia(mediaId!!)
    }

    fun checkMediaStatus() {
        if (mediaId != null) mediaRepository.checkMediaStatus(mediaId!!)
    }
}