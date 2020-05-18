package com.zen.alchan.ui.mangalist

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.ListStyleRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.helper.pojo.MediaFilteredData
import com.zen.alchan.helper.pojo.MediaListTabItem
import type.MediaListStatus
import type.MediaType
import type.ScoreFormat

class MangaListViewModel(private val mediaListRepository: MediaListRepository,
                         private val listStyleRepository: ListStyleRepository,
                         private val userRepository: UserRepository,
                         val gson: Gson): ViewModel() {

    var tabItemList = ArrayList<MediaListTabItem>()
    var selectedTab = 0

    var currentList = ArrayList<MediaList>()

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

    val updateMangaListEntryResponse by lazy {
        mediaListRepository.updateMangaListEntryResponse
    }

    val allowAdultContent: Boolean
        get() = userRepository.viewerData.value?.options?.displayAdultContent ?: false

    val scoreFormat: ScoreFormat
        get() = userRepository.viewerData.value?.mediaListOptions?.scoreFormat ?: ScoreFormat.POINT_100

    val advancedScoringList: ArrayList<String?>
        get() = if (userRepository.viewerData.value?.mediaListOptions?.mangaList?.advancedScoringEnabled == true) {
            ArrayList(userRepository.viewerData.value?.mediaListOptions?.mangaList?.advancedScoring!!)
        } else {
            ArrayList()
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

    fun getTabItemArray(): Array<String> {
        val tabItemArrayList = ArrayList<String>()
        tabItemList.forEach { tabItemArrayList.add("${it.status} (${it.count})") }
        return tabItemArrayList.toTypedArray()
    }

    fun getSelectedList(): ArrayList<MediaList> {
        val selectedList = mangaListData.value?.lists?.find { it.name == tabItemList[selectedTab].status }?.entries

        return if (!selectedList.isNullOrEmpty()) {
            ArrayList(selectedList)
        } else {
            ArrayList()
        }
    }
}