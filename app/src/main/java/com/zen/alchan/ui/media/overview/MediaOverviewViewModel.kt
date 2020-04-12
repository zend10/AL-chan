package com.zen.alchan.ui.media.overview

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.helper.pojo.KeyValueItem
import com.zen.alchan.helper.pojo.MediaRelations

class MediaOverviewViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var mediaId: Int? = null
    var studioList = ArrayList<KeyValueItem>()
    var producerList = ArrayList<KeyValueItem>()
    var relationsList = ArrayList<MediaRelations>()

    val mediaData: MediaQuery.Media?
        get() = if (mediaId != null) mediaRepository.savedMediaData[mediaId!!] else null
}