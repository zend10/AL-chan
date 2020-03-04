package com.zen.alchan.ui.animelist

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.MediaList
import type.ScoreFormat

class AnimeListItemViewModel(private val mediaListRepository: MediaListRepository,
                             private val userRepository: UserRepository
) : ViewModel() {

    var isInit = false
    var selectedFormat: String? = null

    val animeListData by lazy {
        mediaListRepository.animeListData
    }

    val scoreFormat: ScoreFormat
        get() = userRepository.viewerData.value?.mediaListOptions?.scoreFormat ?: ScoreFormat.POINT_100

    fun getSelectedList(): List<MediaList>? {
        return animeListData.value?.lists?.find { it.name == selectedFormat }?.entries
    }
}