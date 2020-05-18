package com.zen.alchan.ui.profile.stats.details

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.SearchRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.repository.UserStatisticRepository
import com.zen.alchan.helper.enums.StatsCategory
import com.zen.alchan.helper.pojo.UserStatsData
import type.MediaType
import type.UserStatisticsSort

class StatsDetailViewModel(private val userStatisticRepository: UserStatisticRepository,
                           private val searchRepository: SearchRepository
): ViewModel() {

    var selectedCategory: StatsCategory? = null
    var selectedMedia: MediaType? = null
    var selectedStatsSort: UserStatisticsSort? = null

    var currentStats: UserStatsData? = null

    val formatStatisticResponse by lazy {
        userStatisticRepository.formatStatisticResponse
    }

    val statusStatisticResponse by lazy {
        userStatisticRepository.statusStatisticResponse
    }

    val scoreStatisticResponse by lazy {
        userStatisticRepository.scoreStatisticResponse
    }

    val lengthStatisticResponse by lazy {
        userStatisticRepository.lengthStatisticResponse
    }

    val releaseYearStatisticResponse by lazy {
        userStatisticRepository.releaseYearStatisticResponse
    }

    val startYearStatisticResponse by lazy {
        userStatisticRepository.startYearStatisticResponse
    }

    val genreStatisticResponse by lazy {
        userStatisticRepository.genreStatisticResponse
    }

    val tagStatisticResponse by lazy {
        userStatisticRepository.tagStatisticResponse
    }

    val countryStatisticResponse by lazy {
        userStatisticRepository.countryStatisticResponse
    }

    val voiceActorStatisticResponse by lazy {
        userStatisticRepository.voiceActorStatisticResponse
    }

    val staffStatisticResponse by lazy {
        userStatisticRepository.staffStatisticResponse
    }

    val studioStatisticResponse by lazy {
        userStatisticRepository.studioStatisticResponse
    }

    fun getStatisticData() {
        if (selectedCategory == null || selectedStatsSort == null) {
            return
        }

        val sort = listOf(selectedStatsSort!!)

        when (selectedCategory) {
            StatsCategory.FORMAT -> userStatisticRepository.getFormatStatistic(sort)
            StatsCategory.STATUS -> userStatisticRepository.getStatusStatistic(sort)
            StatsCategory.SCORE -> userStatisticRepository.getScoreStatistic(sort)
            StatsCategory.LENGTH -> userStatisticRepository.getLengthStatistic(sort)
            StatsCategory.RELEASE_YEAR -> userStatisticRepository.getReleaseYearStatistic(sort)
            StatsCategory.START_YEAR -> userStatisticRepository.getStartYearStatistic(sort)
            StatsCategory.GENRE -> userStatisticRepository.getGenreStatistic(sort)
            StatsCategory.TAG -> userStatisticRepository.getTagStatistic(sort)
            StatsCategory.COUNTRY -> userStatisticRepository.getCountryStatistic(sort)
            StatsCategory.VOICE_ACTOR -> userStatisticRepository.getVoiceActorStatistic(sort)
            StatsCategory.STAFF -> userStatisticRepository.getStaffStatistic(sort)
            StatsCategory.STUDIO -> userStatisticRepository.getStudioStatistic(sort)
        }
    }

    fun getDataString(): String {
        if (selectedStatsSort == UserStatisticsSort.COUNT_DESC) return "TITLE COUNT"
        if (selectedStatsSort == UserStatisticsSort.MEAN_SCORE_DESC) return "MEAN SCORE"

        if (selectedStatsSort == UserStatisticsSort.PROGRESS_DESC) {
            if (selectedMedia == MediaType.ANIME) {
                return "HOURS WATCHED"
            } else if (selectedMedia == MediaType.MANGA) {
                return "CHAPTERS READ"
            }
        }

        return ""
    }
}