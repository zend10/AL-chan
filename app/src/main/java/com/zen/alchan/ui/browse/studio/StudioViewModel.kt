package com.zen.alchan.ui.browse.studio

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.pojo.StudioMedia
import com.zen.alchan.helper.replaceUnderscore
import type.MediaSort

class StudioViewModel(private val browseRepository: BrowseRepository,
                      private val userRepository: UserRepository
) : ViewModel() {

    var studioId: Int? = null
    var currentStudioData: StudioQuery.Studio? = null

    var page = 1
    var hasNextPage = true
    var mediaSortIndex = 0

    var isInit = false
    var studioMediaList = ArrayList<StudioMedia?>()

    val mediaSortArray = arrayOf(
        "NEWEST",
        "OLDEST",
        "TITLE ROMAJI",
        "TITLE ENGLISH",
        "TITLE NATIVE",
        "FIRST ADDED",
        "LAST ADDED",
        "HIGHEST SCORE",
        "LOWEST SCORE",
        "MOST POPULAR",
        "LEAST POPULAR",
        "MOST FAVORITE",
        "LEAST FAVORITE",
        "TRENDING"
    )

    var mediaSortList = arrayListOf(
        MediaSort.START_DATE_DESC,
        MediaSort.START_DATE,
        MediaSort.TITLE_ROMAJI,
        MediaSort.TITLE_ENGLISH,
        MediaSort.TITLE_NATIVE,
        MediaSort.ID,
        MediaSort.ID_DESC,
        MediaSort.SCORE_DESC,
        MediaSort.SCORE,
        MediaSort.POPULARITY_DESC,
        MediaSort.POPULARITY,
        MediaSort.FAVOURITES_DESC,
        MediaSort.FAVOURITES,
        MediaSort.TRENDING_DESC
    )

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
        if (hasNextPage && studioId != null) browseRepository.getStudioMedia(studioId!!, page, mediaSortList[mediaSortIndex])
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