package com.zen.alchan.ui.animelist.list

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.MediaList
import type.MediaListStatus
import type.ScoreFormat

class AnimeListItemViewModel(private val mediaListRepository: MediaListRepository,
                             private val userRepository: UserRepository,
                             private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    var isInit = false
    var selectedStatus: String? = null

    val animeListData by lazy {
        mediaListRepository.animeListData
    }

    val scoreFormat: ScoreFormat
        get() = userRepository.viewerData.value?.mediaListOptions?.scoreFormat ?: ScoreFormat.POINT_100

    val advancedScoringList: ArrayList<String>
        get() = if (userRepository.viewerData.value?.mediaListOptions?.animeList?.advancedScoringEnabled == true) {
            ArrayList(userRepository.viewerData.value?.mediaListOptions?.animeList?.advancedScoring!!)
        } else {
            ArrayList()
        }

    fun getSelectedList(): ArrayList<MediaList> {
        val selectedList = animeListData.value?.lists?.find { it.name == selectedStatus }?.entries

        return if (!selectedList.isNullOrEmpty()) {
            ArrayList(selectedList)
        } else {
            ArrayList()
        }
    }

    fun updateAnimeProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        progress: Int
    ) {
        mediaListRepository.updateAnimeProgress(entryId, status, repeat, progress)
    }

    fun updateAnimeScore(entryId: Int, score: Double, advancedScores: List<Double>?) {
        mediaListRepository.updateAnimeScore(entryId, score, advancedScores)
    }
}