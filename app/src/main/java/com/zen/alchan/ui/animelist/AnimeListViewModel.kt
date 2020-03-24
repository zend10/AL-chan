package com.zen.alchan.ui.animelist

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.data.response.MediaListCollection
import com.zen.alchan.data.response.MediaListGroup
import com.zen.alchan.helper.pojo.MediaFilteredData
import com.zen.alchan.helper.pojo.MediaListTabItem

class AnimeListViewModel(private val mediaListRepository: MediaListRepository, val gson: Gson) : ViewModel() {

    var tabItemList = ArrayList<MediaListTabItem>()
    var selectedTab = 0

    val filteredData: MediaFilteredData?
        get() = mediaListRepository.filteredData

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

    fun setFilteredData(newFilteredData: MediaFilteredData?) {
        mediaListRepository.handleNewFilter(newFilteredData)
    }
}