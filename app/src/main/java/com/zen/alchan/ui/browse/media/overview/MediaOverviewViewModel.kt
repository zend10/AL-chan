package com.zen.alchan.ui.browse.media.overview

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.helper.pojo.*

class MediaOverviewViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var mediaId: Int? = null
    var charactersList = ArrayList<MediaCharacters>()
    var studioList = ArrayList<KeyValueItem>()
    var producerList = ArrayList<KeyValueItem>()
    var tagsList = ArrayList<MediaTags>()
    var showSpoiler = false
    var relationsList = ArrayList<MediaRelations>()
    var recommendationsList = ArrayList<MediaRecommendations>()
    var linksList = ArrayList<MediaLinks>()

    val mediaData: MediaQuery.Media?
        get() = if (mediaId != null) mediaRepository.savedMediaData[mediaId!!] else null
}