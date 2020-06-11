package com.zen.alchan.ui.settings.list

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.MediaListTypeOptions
import type.ScoreFormat

class ListSettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

    val scoringFormatStringArray = arrayOf(
        "100 Point (55/100)", "10 Point Decimal (5.5/10)", "10 Point (5/10)", "5 Star (3/5)", "3 Point Smiley :)"
    )

    val scoringFormatList = listOf(
        ScoreFormat.POINT_100, ScoreFormat.POINT_10_DECIMAL, ScoreFormat.POINT_10, ScoreFormat.POINT_5, ScoreFormat.POINT_3
    )

    val defaultListOrderStringArray = arrayOf(
        "Title", "Score", "Last Updated", "Last Added"
    )

    val defaultListOrderList = listOf(
        "title", "score", "updatedAt", "id"
    )

    var selectedScoringSystem: ScoreFormat? = null
    var selectedDefaultListOrder: String? = null

    var advancedScoringLists = ArrayList<String?>()
    var customAnimeLists = ArrayList<String?>()
    var customMangaLists = ArrayList<String?>()

    var animeSectionOrder = ArrayList<String?>()
    var mangaSectionOrder = ArrayList<String?>()

    var isInit = false

    val viewerData by lazy {
        userRepository.viewerData
    }

    val updateListSettingsResponse by lazy {
        userRepository.updateListSettingsResponse
    }

    fun initData() {
        userRepository.getViewerData()

        if (selectedScoringSystem == null) {
            selectedScoringSystem = userRepository.viewerData.value?.mediaListOptions?.scoreFormat
        }

        if (selectedDefaultListOrder == null) {
            selectedDefaultListOrder = userRepository.viewerData.value?.mediaListOptions?.rowOrder
        }

        if (!isInit) {
            val savedAdvancedScoringList = userRepository.viewerData.value?.mediaListOptions?.animeList?.advancedScoring
            if (!savedAdvancedScoringList.isNullOrEmpty()) {
                advancedScoringLists.addAll(savedAdvancedScoringList)
            }

            val savedCustomAnimeList = userRepository.viewerData.value?.mediaListOptions?.animeList?.customLists
            if (!savedCustomAnimeList.isNullOrEmpty()) {
                customAnimeLists.addAll(savedCustomAnimeList)
            }

            val savedCustomMangaList = userRepository.viewerData.value?.mediaListOptions?.mangaList?.customLists
            if (!savedCustomMangaList.isNullOrEmpty()) {
                customMangaLists.addAll(savedCustomMangaList)
            }

            val savedAnimeSectionOrder = userRepository.viewerData.value?.mediaListOptions?.animeList?.sectionOrder
            if (!savedAnimeSectionOrder.isNullOrEmpty()) {
                animeSectionOrder.addAll(savedAnimeSectionOrder)
            }

            val savedMangaSectionOrder = userRepository.viewerData.value?.mediaListOptions?.mangaList?.sectionOrder
            if (!savedMangaSectionOrder.isNullOrEmpty()) {
                mangaSectionOrder.addAll(savedMangaSectionOrder)
            }
        }
    }

    fun updateListSettings(useAdvancedScoring: Boolean, splitAnimeList: Boolean, splitMangaList: Boolean) {
        if (selectedScoringSystem == null || selectedDefaultListOrder == null) {
             return
        }

        val animeListOptions = MediaListTypeOptions(
            animeSectionOrder,
            splitAnimeList,
            customAnimeLists,
            advancedScoringLists,
            useAdvancedScoring
        )

        val mangaListOptions = MediaListTypeOptions(
            mangaSectionOrder,
            splitMangaList,
            customMangaLists,
            advancedScoringLists,
            useAdvancedScoring
        )

        userRepository.updateListSettings(selectedScoringSystem!!, selectedDefaultListOrder!!, animeListOptions, mangaListOptions)
    }
}