package com.zen.alchan.ui.animelist

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.helper.pojo.MediaListTabItem

class AnimeListViewModel(private val mediaListRepository: MediaListRepository) : ViewModel() {

    var tabItemList = ArrayList<MediaListTabItem>()
    var selectedTab = 0

    val animeListDataResponse by lazy {
        mediaListRepository.animeListDataResponse
    }

    val animeListData by lazy {
        mediaListRepository.animeListData
    }

    fun retrieveAnimeListData() {
        mediaListRepository.retrieveAnimeListData()
    }

    fun initData() {
        mediaListRepository.retrieveAnimeListData()
    }
}