package com.zen.alchan.ui.home

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.utils.Utility

class HomeViewModel(private val userRepository: UserRepository,
                    private val appSettingsRepository: AppSettingsRepository,
                    private val mediaRepository: MediaRepository
) : ViewModel() {

    var isInit = false
    var page = 1
    var hasNextPage = true
    var releasingTodayList = ArrayList<HomeFragment.ReleasingTodayItem>()

    var trendingAnimeList = ArrayList<HomeFragment.TrendingMediaItem>()
    var trendingMangaList = ArrayList<HomeFragment.TrendingMediaItem>()

    var explorePageArray = arrayOf(
        BrowsePage.ANIME.name, BrowsePage.MANGA.name, BrowsePage.CHARACTER.name, BrowsePage.STAFF.name, BrowsePage.STUDIO.name
    )

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

    val releasingTodayData by lazy {
        mediaRepository.releasingTodayData
    }

    fun initData() {
        userRepository.getViewerData()
        mediaRepository.getTrendingAnime()
        mediaRepository.getTrendingManga()

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