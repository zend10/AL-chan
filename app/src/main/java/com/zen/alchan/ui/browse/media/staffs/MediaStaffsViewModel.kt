package com.zen.alchan.ui.browse.media.staffs

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.helper.pojo.MediaStaffs
import type.MediaType

class MediaStaffsViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var mediaId: Int? = null
    var mediaType: MediaType? = null
    var page = 1
    var hasNextPage = true

    var isInit = false
    var mediaStaffs = ArrayList<MediaStaffs?>()

    val mediaStaffsData by lazy {
        mediaRepository.mediaStaffsData
    }

    fun getMediaStaffs() {
        if (hasNextPage && mediaId != null) mediaRepository.getMediaStaffs(mediaId!!, page)
    }
}