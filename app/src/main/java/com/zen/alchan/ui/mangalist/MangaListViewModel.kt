package com.zen.alchan.ui.mangalist

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.ListStyleRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.helper.pojo.MediaFilteredData
import com.zen.alchan.helper.pojo.MediaListTabItem
import type.MediaType

class MangaListViewModel(private val mediaListRepository: MediaListRepository,
                         private val listStyleRepository: ListStyleRepository,
                         val gson: Gson): ViewModel() {

    var tabItemList = ArrayList<MediaListTabItem>()
    var selectedTab = 0

    val filteredData: MediaFilteredData?
        get() = mediaListRepository.mangaFilteredData

    val mangaListDataResponse by lazy {
        mediaListRepository.mangaListDataResponse
    }

    val mangaListData by lazy {
        mediaListRepository.mangaListData
    }

    val mangaListStyleLiveData by lazy {
        listStyleRepository.mangaListStyleLiveData
    }

    fun retrieveMangaListData() {
        mediaListRepository.retrieveMangaListData()
    }

    fun initData() {
        mediaListRepository.retrieveMangaListData()
        listStyleRepository.saveMangaListStyle(listStyleRepository.mangaListStyle)
    }

    fun setFilteredData(newFilteredData: MediaFilteredData?) {
        mediaListRepository.handleNewFilter(newFilteredData, MediaType.MANGA)
    }
}