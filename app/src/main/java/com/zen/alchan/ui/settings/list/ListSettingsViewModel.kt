package com.zen.alchan.ui.settings.list

import androidx.lifecycle.ViewModel
import com.zen.alchan.R
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.MediaListTypeOptions
import type.ScoreFormat

class ListSettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

    val scoreFormatMap = hashMapOf(
        Pair(ScoreFormat.POINT_100, R.string.hundred_point),
        Pair(ScoreFormat.POINT_10_DECIMAL, R.string.ten_point_decimal),
        Pair(ScoreFormat.POINT_10, R.string.ten_point),
        Pair(ScoreFormat.POINT_5, R.string.five_star),
        Pair(ScoreFormat.POINT_3, R.string.three_point_smiley)
    )

    val defaultListOrderMap = hashMapOf(
        Pair("title", R.string.title),
        Pair("score", R.string.score),
        Pair("updatedAt", R.string.last_updated),
        Pair("id", R.string.last_added)
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
            selectedScoringSystem = userRepository.currentUser?.mediaListOptions?.scoreFormat
        }

        if (selectedDefaultListOrder == null) {
            selectedDefaultListOrder = userRepository.currentUser?.mediaListOptions?.rowOrder
        }

        if (!isInit) {
            val savedAdvancedScoringList = userRepository.currentUser?.mediaListOptions?.animeList?.advancedScoring
            if (!savedAdvancedScoringList.isNullOrEmpty()) {
                advancedScoringLists.addAll(savedAdvancedScoringList)
            }

            val savedCustomAnimeList = userRepository.currentUser?.mediaListOptions?.animeList?.customLists
            if (!savedCustomAnimeList.isNullOrEmpty()) {
                customAnimeLists.addAll(savedCustomAnimeList)
            }

            val savedCustomMangaList = userRepository.currentUser?.mediaListOptions?.mangaList?.customLists
            if (!savedCustomMangaList.isNullOrEmpty()) {
                customMangaLists.addAll(savedCustomMangaList)
            }

            val savedAnimeSectionOrder = userRepository.currentUser?.mediaListOptions?.animeList?.sectionOrder
            if (!savedAnimeSectionOrder.isNullOrEmpty()) {
                animeSectionOrder.addAll(savedAnimeSectionOrder)
            }

            val savedMangaSectionOrder = userRepository.currentUser?.mediaListOptions?.mangaList?.sectionOrder
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