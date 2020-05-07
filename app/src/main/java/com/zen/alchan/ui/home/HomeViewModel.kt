package com.zen.alchan.ui.home

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.utils.Utility

class HomeViewModel(private val userRepository: UserRepository,
                    private val appSettingsRepository: AppSettingsRepository,
                    private val mediaRepository: MediaRepository
) : ViewModel() {

    var isInit = false
    var page = 1
    var hasNextPage = true
    var releasingTodayList = ArrayList<HomeFragment.ReleasingTodayItem>()

    var popularThisSeasonList = ArrayList<PopularSeasonQuery.Medium>()
    var trendingAnimeList = ArrayList<HomeFragment.TrendingMediaItem>()
    var trendingMangaList = ArrayList<HomeFragment.TrendingMediaItem>()

    val viewerDataResponse by lazy {
        userRepository.viewerDataResponse
    }

    val viewerData by lazy {
        userRepository.viewerData
    }

    val trendingAnimeData by lazy {
        mediaRepository.trendingAnimeData
    }

    val trendingMangaData by lazy {
        mediaRepository.trendingMangaData
    }

    val popularThisSeasonData by lazy {
        mediaRepository.popularThisSeasonData
    }

    val releasingTodayData by lazy {
        mediaRepository.releasingTodayData
    }

    fun initData() {
        userRepository.getViewerData()
        mediaRepository.getTrendingAnime()
        mediaRepository.getTrendingManga()
        mediaRepository.getPopularThisSeason()

        if (Utility.timeDiffMoreThanOneDay(userRepository.viewerDataLastRetrieved)) {
            userRepository.retrieveViewerData()
        }

        if (Utility.timeDiffMoreThanOneDay(mediaRepository.genreListLastRetrieved) || mediaRepository.genreList.isNullOrEmpty()) {
            mediaRepository.getGenre()
        }
    }

    fun getReleasingToday() {
        if (hasNextPage) mediaRepository.getReleasingToday(page)
    }
}