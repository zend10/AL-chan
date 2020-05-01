package com.zen.alchan.ui.browse.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.helper.enums.MediaPage
import type.MediaType

class MediaViewModel(private val mediaRepository: MediaRepository) : ViewModel() {

    private val _currentSection = MutableLiveData<MediaPage>()
    val currentSection: LiveData<MediaPage>
        get() = _currentSection

    var mediaId: Int? = null
    var mediaType: MediaType? = null

    var currentMediaData: MediaQuery.Media? = null

    val mediaData by lazy {
        mediaRepository.mediaData
    }

    val mediaStatus by lazy {
        mediaRepository.mediaStatus
    }

    fun initData() {
        getMedia()

        if (currentSection.value == null) {
            _currentSection.postValue(MediaPage.OVERVIEW)
        }
    }

    fun getMedia() {
        if (mediaId != null) mediaRepository.getMedia(mediaId!!)
    }

    fun checkMediaStatus() {
        if (mediaId != null) mediaRepository.checkMediaStatus(mediaId!!)
    }

    fun setMediaSection(section: MediaPage) {
        _currentSection.postValue(section)
    }

    fun refreshData() {
        if (mediaId != null) {
            getMedia()
            checkMediaStatus()
            mediaRepository.getMediaOverview(mediaId!!)
        }
    }
}