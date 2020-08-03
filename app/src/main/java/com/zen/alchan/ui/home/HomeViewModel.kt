package com.zen.alchan.ui.home

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.MediaRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.pojo.Review
import com.zen.alchan.helper.utils.Utility
import type.MediaListStatus
import type.ReviewSort

class HomeViewModel(private val userRepository: UserRepository,
                    private val appSettingsRepository: AppSettingsRepository,
                    private val mediaRepository: MediaRepository,
                    private val mediaListRepository: MediaListRepository
) : ViewModel() {

    var isInit = false
    var page = 1
    var hasNextPage = true
    var releasingTodayList = ArrayList<HomeFragment.ReleasingTodayItem>()

    var trendingAnimeList = ArrayList<HomeFragment.TrendingMediaItem>()
    var trendingMangaList = ArrayList<HomeFragment.TrendingMediaItem>()
    var recentReviewsList = ArrayList<Review>()

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

    val recentReviewsData by lazy {
        mediaRepository.recentReviewsData
    }

    val animeListData by lazy {
        mediaListRepository.animeListData
    }

    val circularAvatar
        get() = appSettingsRepository.appSettings.circularAvatar == true

    val whiteBackgroundAvatar
        get() = appSettingsRepository.appSettings.whiteBackgroundAvatar == true

    val showRecentReviews: Boolean
        get() = appSettingsRepository.appSettings.showRecentReviews == true

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

        if (showRecentReviews) {
            mediaRepository.getReviews(1, 10, null, listOf(ReviewSort.CREATED_AT_DESC), true)
        }

        // TODO: uncomment later after search by tag is implemented
//        if (Utility.timeDiffMoreThanOneDay(mediaRepository.tagListLastRetrieved) || mediaRepository.tagList.isNullOrEmpty()) {
//            mediaRepository.getTag()
//        }
    }

    fun getReleasingToday() {
        if (hasNextPage) mediaRepository.getReleasingToday(page)
    }

    fun getNotificationCount() {
        userRepository.getNotificationCount()
    }

    fun updateAnimeProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        progress: Int
    ) {
        mediaListRepository.updateAnimeProgress(entryId, status, repeat, progress)
    }
}