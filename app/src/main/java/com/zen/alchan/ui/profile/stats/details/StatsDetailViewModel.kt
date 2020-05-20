package com.zen.alchan.ui.profile.stats.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zen.alchan.data.repository.SearchRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.repository.UserStatisticRepository
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.StatsCategory
import com.zen.alchan.helper.pojo.UserStatsData
import com.zen.alchan.helper.replaceUnderscore
import type.MediaListStatus
import type.MediaType
import type.UserStatisticsSort

class StatsDetailViewModel(private val userStatisticRepository: UserStatisticRepository,
                           private val searchRepository: SearchRepository
): ViewModel() {

    var selectedCategory: StatsCategory? = null
    var selectedMedia: MediaType? = null
    var selectedStatsSort: UserStatisticsSort? = null

    var currentStats: ArrayList<UserStatsData>? = null
    var currentMediaList: ArrayList<MediaImageQuery.Medium?>? = null

    val sortDataList = arrayListOf(
        UserStatisticsSort.COUNT_DESC,
        UserStatisticsSort.PROGRESS_DESC,
        UserStatisticsSort.MEAN_SCORE_DESC
    )

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

    val searchMediaImageResponse by lazy {
        userStatisticRepository.searchMediaImageResponse
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

    fun getSortString(): String {
        if (selectedStatsSort == UserStatisticsSort.COUNT_DESC) return "TITLE COUNT"
        if (selectedStatsSort == UserStatisticsSort.MEAN_SCORE_DESC) return "MEAN SCORE"

        if (selectedStatsSort == UserStatisticsSort.PROGRESS_DESC) {
            if (selectedMedia == MediaType.ANIME) {
                return "TIME WATCHED"
            } else if (selectedMedia == MediaType.MANGA) {
                return "CHAPTERS READ"
            }
        }

        return ""
    }

    fun getStatsCategoryArray(): Array<String> {
        return StatsCategory.values().map { it.name.replaceUnderscore() }.toTypedArray()
    }

    fun getMediaTypeArray(): Array<String> {
        return MediaType.values().filter { it != MediaType.UNKNOWN__ }.map { it.name }.toTypedArray()
    }

    fun getSortDataArray(): Array<String> {
        val progressLabel = if (selectedMedia == MediaType.ANIME) {
            "TIME WATCHED"
        } else {
            "CHAPTERS READ"
        }

        return arrayOf("TITLE COUNT", progressLabel, "MEAN SCORE")
    }

    fun searchMediaImage(page: Int = 1) {
        if (currentStats.isNullOrEmpty()) {
            return
        }

        val idIn = HashSet<Int>()

        // only take 6 from each entry
        currentStats?.forEach {
            if (it.mediaIds?.isNullOrEmpty() == false) {
                val idNotNull = it.mediaIds.filterNotNull()
                if (idNotNull.size < 6) {
                    idIn.addAll(idNotNull)
                } else {
                    idIn.addAll(idNotNull.take(6))
                }
            }
        }

        userStatisticRepository.searchMediaImage(page, idIn.toList())
    }
}