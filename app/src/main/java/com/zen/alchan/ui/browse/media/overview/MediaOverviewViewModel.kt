package com.zen.alchan.ui.browse.media.overview

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.InfoRepository
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.data.response.AnimeDetails
import com.zen.alchan.data.response.MangaDetails
import com.zen.alchan.helper.pojo.*
import type.MediaType
import java.net.URLEncoder

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
    var trailersList = ArrayList<MediaTrailer>()
    var linksList = ArrayList<MediaLinks>()

    var animeDetails: AnimeDetails? = null
    var mangaDetails: MangaDetails? = null

    val mediaOverviewData by lazy {
        mediaRepository.mediaOverviewData
    }

    val animeDetailsLiveData by lazy {
        mediaRepository.animeDetailsLiveData
    }

    val mangaDetailsLiveData by lazy {
        mediaRepository.mangaDetailsLiveData
    }

    val animeVideoLiveData by lazy {
        mediaRepository.animeVideoLiveData
    }



    fun getMediaOverview() {
        if (mediaId != null) mediaRepository.getMediaOverview(mediaId!!)
    }

    fun getAnimeThemes() {
        if (mediaId != null && mediaData?.type == MediaType.ANIME && mediaData?.idMal != null) {
            mediaRepository.getAnimeDetails(mediaData?.idMal!!)
        }
    }

    fun getMangaPublisher() {
        if (mediaId != null && mediaData?.type == MediaType.MANGA && mediaData?.idMal != null) {
            mediaRepository.getMangaDetails(mediaData?.idMal!!)
        }
    }

    fun getAnimeVideos() {
        if (mediaId != null && mediaData?.type == MediaType.ANIME && mediaData?.idMal != null) {
            mediaRepository.getAnimeVideos(mediaData?.idMal!!)
        }
    }
}