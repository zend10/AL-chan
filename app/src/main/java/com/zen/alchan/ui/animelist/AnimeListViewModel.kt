package com.zen.alchan.ui.animelist

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.ListStyleRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.helper.pojo.MediaFilteredData
import com.zen.alchan.helper.pojo.MediaListTabItem
import type.MediaType

class AnimeListViewModel(private val mediaListRepository: MediaListRepository,
                         private val listStyleRepository: ListStyleRepository,
                         val gson: Gson) : ViewModel() {

    var tabItemList = ArrayList<MediaListTabItem>()
    var selectedTab = 0

    val filteredData: MediaFilteredData?
        get() = mediaListRepository.animeFilteredData

    val animeListDataResponse by lazy {
        mediaListRepository.animeListDataResponse
    }

    val animeListData by lazy {
        mediaListRepository.animeListData
    }

    val animeListStyleLiveData by lazy {
        listStyleRepository.animeListStyleLiveData
    }

    fun retrieveAnimeListData() {
        mediaListRepository.retrieveAnimeListData()
    }

    fun initData() {
        mediaListRepository.retrieveAnimeListData()
        listStyleRepository.saveAnimeListStyle(listStyleRepository.animeListStyle)
    }

    fun setFilteredData(newFilteredData: MediaFilteredData?) {
        mediaListRepository.handleNewFilter(newFilteredData, MediaType.ANIME)
    }

    fun setShouldLoading(shouldLoading: Boolean) {
        mediaListRepository.setShouldLoading(shouldLoading)
    }
}