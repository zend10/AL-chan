package com.zen.alchan.ui.browse.studio

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.pojo.StudioMedia
import type.MediaSort

class StudioViewModel(private val browseRepository: BrowseRepository,
                      private val userRepository: UserRepository
) : ViewModel() {

    var studioId: Int? = null
    var currentStudioData: StudioQuery.Studio? = null

    var page = 1
    var hasNextPage = true
    var mediaSort = MediaSort.START_DATE_DESC

    var isInit = false
    var studioMediaList = ArrayList<StudioMedia>()

    val studioData by lazy {
        browseRepository.studioData
    }

    val studioMediaData by lazy {
        browseRepository.studioMediaData
    }

    val studioIsFavoriteData by lazy {
        browseRepository.studioIsFavoriteData
    }

    val toggleFavouriteResponse by lazy {
        userRepository.toggleFavouriteResponse
    }

    fun getStudio() {
        if (studioId != null) browseRepository.getStudio(studioId!!)
    }

    fun getStudioMedia() {
        if (hasNextPage && studioId != null) browseRepository.getStudioMedia(studioId!!, page, mediaSort)
    }

    fun checkStudioIsFavorite() {
        if (studioId != null) browseRepository.checkStudioIsFavorite(studioId!!)
    }

    fun updateFavorite() {
        if (studioId != null) {
            userRepository.toggleFavourite(null, null, null, null, studioId)
        }
    }
}