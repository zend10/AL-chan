package com.zen.alchan.ui.mangalist.list

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.ListStyleRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.MediaList
import type.MediaListStatus
import type.ScoreFormat

class MangaListItemViewModel(private val mediaListRepository: MediaListRepository,
                             private val userRepository: UserRepository,
                             private val listStyleRepository: ListStyleRepository,
                             val gson: Gson
) : ViewModel() {

    var isInit = false
    var selectedStatus: String? = null

    val mangaListData by lazy {
        mediaListRepository.mangaListData
    }

    val scoreFormat: ScoreFormat
        get() = userRepository.viewerData.value?.mediaListOptions?.scoreFormat ?: ScoreFormat.POINT_100

    val advancedScoringList: ArrayList<String?>
        get() = if (userRepository.viewerData.value?.mediaListOptions?.mangaList?.advancedScoringEnabled == true) {
            ArrayList(userRepository.viewerData.value?.mediaListOptions?.mangaList?.advancedScoring!!)
        } else {
            ArrayList()
        }

    val mangaListStyleLiveData by lazy {
        listStyleRepository.mangaListStyleLiveData
    }

    fun getSelectedList(): ArrayList<MediaList> {
        val selectedList = mangaListData.value?.lists?.find { it.name == selectedStatus }?.entries

        return if (!selectedList.isNullOrEmpty()) {
            ArrayList(selectedList)
        } else {
            ArrayList()
        }
    }

    fun updateMangaProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        newProgress: Int,
        isVolume: Boolean
    ) {
        if (isVolume) {
            mediaListRepository.updateMangaProgress(entryId, status, repeat, null, newProgress)
        } else {
            mediaListRepository.updateMangaProgress(entryId, status, repeat, newProgress, null)
        }
    }

    fun updateMangaScore(entryId: Int, score: Double, advancedScores: List<Double>?) {
        mediaListRepository.updateMangaScore(entryId, score, advancedScores)
    }
}