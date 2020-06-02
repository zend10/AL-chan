package com.zen.alchan.ui.browse.user.stats

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.OtherUserStatisticRepository
import com.zen.alchan.helper.enums.StatsCategory
import com.zen.alchan.helper.pojo.UserStatsData
import com.zen.alchan.helper.replaceUnderscore
import type.MediaType
import type.UserStatisticsSort

class UserStatsDetailViewModel(private val otherUserStatisticRepository: OtherUserStatisticRepository) : ViewModel() {

    var otherUserId: Int? = null
    var username: String? = null

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
        otherUserStatisticRepository.formatStatisticResponse
    }

    val statusStatisticResponse by lazy {
        otherUserStatisticRepository.statusStatisticResponse
    }

    val scoreStatisticResponse by lazy {
        otherUserStatisticRepository.scoreStatisticResponse
    }

    val lengthStatisticResponse by lazy {
        otherUserStatisticRepository.lengthStatisticResponse
    }

    val releaseYearStatisticResponse by lazy {
        otherUserStatisticRepository.releaseYearStatisticResponse
    }

    val startYearStatisticResponse by lazy {
        otherUserStatisticRepository.startYearStatisticResponse
    }

    val genreStatisticResponse by lazy {
        otherUserStatisticRepository.genreStatisticResponse
    }

    val tagStatisticResponse by lazy {
        otherUserStatisticRepository.tagStatisticResponse
    }

    val countryStatisticResponse by lazy {
        otherUserStatisticRepository.countryStatisticResponse
    }

    val voiceActorStatisticResponse by lazy {
        otherUserStatisticRepository.voiceActorStatisticResponse
    }

    val staffStatisticResponse by lazy {
        otherUserStatisticRepository.staffStatisticResponse
    }

    val studioStatisticResponse by lazy {
        otherUserStatisticRepository.studioStatisticResponse
    }

    val searchMediaImageResponse by lazy {
        otherUserStatisticRepository.searchMediaImageResponse
    }

    fun getStatisticData() {
        if (otherUserId == null || selectedCategory == null || selectedStatsSort == null) {
            return
        }

        val sort = listOf(selectedStatsSort!!)

        when (selectedCategory) {
            StatsCategory.FORMAT -> otherUserStatisticRepository.getFormatStatistic(otherUserId!!, sort)
            StatsCategory.STATUS -> otherUserStatisticRepository.getStatusStatistic(otherUserId!!, sort)
            StatsCategory.SCORE -> otherUserStatisticRepository.getScoreStatistic(otherUserId!!, sort)
            StatsCategory.LENGTH -> otherUserStatisticRepository.getLengthStatistic(otherUserId!!, sort)
            StatsCategory.RELEASE_YEAR -> otherUserStatisticRepository.getReleaseYearStatistic(otherUserId!!, sort)
            StatsCategory.START_YEAR -> otherUserStatisticRepository.getStartYearStatistic(otherUserId!!, sort)
            StatsCategory.GENRE -> otherUserStatisticRepository.getGenreStatistic(otherUserId!!, sort)
            StatsCategory.TAG -> otherUserStatisticRepository.getTagStatistic(otherUserId!!, sort)
            StatsCategory.COUNTRY -> otherUserStatisticRepository.getCountryStatistic(otherUserId!!, sort)
            StatsCategory.VOICE_ACTOR -> otherUserStatisticRepository.getVoiceActorStatistic(otherUserId!!, sort)
            StatsCategory.STAFF -> otherUserStatisticRepository.getStaffStatistic(otherUserId!!, sort)
            StatsCategory.STUDIO -> otherUserStatisticRepository.getStudioStatistic(otherUserId!!, sort)
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

        otherUserStatisticRepository.searchMediaImage(page, idIn.toList())
    }
}