package com.zen.alchan.ui.browse.media.stats

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository

class MediaStatsViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var mediaId: Int? = null
    var mediaData: MediaStatsQuery.Media? = null

    val mediaStatsData by lazy {
        mediaRepository.mediaStatsData
    }

    fun getMediaStats() {
        if (mediaId != null) mediaRepository.getMediaStats(mediaId!!)
    }
}