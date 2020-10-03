package com.zen.alchan.ui.browse.media.overview

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.data.response.MangaDetails
import com.zen.alchan.helper.pojo.*
import type.MediaType

class MediaOverviewViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    var mediaId: Int? = null
    var mediaData: MediaOverviewQuery.Media? = null

    var charactersList = ArrayList<MediaCharacters>()
    var studioList = ArrayList<KeyValueItem>()
    var producerList = ArrayList<KeyValueItem>()
    var tagsList = ArrayList<MediaTags>()
    var showSpoiler = false
    var relationsList = ArrayList<MediaRelations>()
    var recommendationsList = ArrayList<MediaRecommendations>()
    var linksList = ArrayList<MediaLinks>()

    var mangaDetails: MangaDetails? = null

    val mediaOverviewData by lazy {
        mediaRepository.mediaOverviewData
    }

    val mangaDetailsLiveData by lazy {
        mediaRepository.mangaDetailsLiveData
    }

    fun getMediaOverview() {
        if (mediaId != null) mediaRepository.getMediaOverview(mediaId!!)
    }

    fun getMangaPublisher() {
        if (mediaId != null && mediaData?.type == MediaType.MANGA && mediaData?.idMal != null) {
            mediaRepository.getMangaDetails(mediaData?.idMal!!)
        }
    }
}