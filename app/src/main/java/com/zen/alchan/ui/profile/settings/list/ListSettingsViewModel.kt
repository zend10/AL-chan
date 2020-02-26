package com.zen.alchan.ui.profile.settings.list

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.ProfileRepository
import com.zen.alchan.data.response.MediaListTypeOptions
import type.ScoreFormat

class ListSettingsViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

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

    var advancedScoringLists = ArrayList<String>()
    var customAnimeLists = ArrayList<String>()
    var customMangaLists = ArrayList<String>()

    var isInit = false

    val viewerData by lazy {
        profileRepository.viewerData
    }

    val updateListSettingsResponse by lazy {
        profileRepository.updateListSettingsResponse
    }

    fun initData() {
        profileRepository.getViewerData()

        if (selectedScoringSystem == null) {
            selectedScoringSystem = profileRepository.viewerData.value?.mediaListOptions?.scoreFormat
        }

        if (selectedDefaultListOrder == null) {
            selectedDefaultListOrder = profileRepository.viewerData.value?.mediaListOptions?.rowOrder
        }

        if (!isInit) {
            val savedAdvancedScoringList = profileRepository.viewerData.value?.mediaListOptions?.animeList?.advancedScoring
            if (!savedAdvancedScoringList.isNullOrEmpty()) {
                advancedScoringLists.addAll(savedAdvancedScoringList)
            }

            val savedCustomAnimeList = profileRepository.viewerData.value?.mediaListOptions?.animeList?.customLists
            if (!savedCustomAnimeList.isNullOrEmpty()) {
                customAnimeLists.addAll(savedCustomAnimeList)
            }

            val savedCustomMangaList = profileRepository.viewerData.value?.mediaListOptions?.mangaList?.customLists
            if (!savedCustomMangaList.isNullOrEmpty()) {
                customMangaLists.addAll(savedCustomMangaList)
            }
        }
    }

    fun updateListSettings(useAdvancedScoring: Boolean, splitAnimeList: Boolean, splitMangaList: Boolean) {
        if (selectedScoringSystem == null || selectedDefaultListOrder == null) {
             return
        }

        val animeListOptions = MediaListTypeOptions(
            null,
            splitAnimeList,
            customAnimeLists,
            advancedScoringLists,
            useAdvancedScoring
        )

        val mangaListOptions = MediaListTypeOptions(
            null,
            splitMangaList,
            customMangaLists,
            advancedScoringLists,
            useAdvancedScoring
        )

        profileRepository.updateListSettings(selectedScoringSystem!!, selectedDefaultListOrder!!, animeListOptions, mangaListOptions)
    }
}