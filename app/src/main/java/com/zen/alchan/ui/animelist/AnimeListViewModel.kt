package com.zen.alchan.ui.animelist

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

class AnimeListViewModel(private val mediaListRepository: MediaListRepository,
                         private val listStyleRepository: ListStyleRepository,
                         private val userRepository: UserRepository,
                         val gson: Gson) : ViewModel() {

    var tabItemList = ArrayList<MediaListTabItem>()
    var selectedTab = 0

    var currentList = ArrayList<MediaList>()

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

    val updateAnimeListEntryResponse by lazy {
        mediaListRepository.updateAnimeListEntryResponse
    }

    val allowAdultContent: Boolean
        get() = userRepository.viewerData.value?.options?.displayAdultContent ?: false

    val scoreFormat: ScoreFormat
        get() = userRepository.viewerData.value?.mediaListOptions?.scoreFormat ?: ScoreFormat.POINT_100

    val advancedScoringEnabled: Boolean
        get() = userRepository.viewerData.value?.mediaListOptions?.animeList?.advancedScoringEnabled == true

    val advancedScoringList: ArrayList<String?>
        get() = if (userRepository.viewerData.value?.mediaListOptions?.animeList?.advancedScoringEnabled == true) {
            ArrayList(userRepository.viewerData.value?.mediaListOptions?.animeList?.advancedScoring!!)
        } else {
            ArrayList()
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

    fun getTabItemArray(): Array<String> {
        val tabItemArrayList = ArrayList<String>()
        tabItemList.forEach { tabItemArrayList.add("${it.status} (${it.count})") }
        return tabItemArrayList.toTypedArray()
    }

    fun getSelectedList(): ArrayList<MediaList> {
        val selectedList = animeListData.value?.lists?.find { it.name == tabItemList[selectedTab].status }?.entries

        return if (!selectedList.isNullOrEmpty()) {
            ArrayList(selectedList)
        } else {
            ArrayList()
        }
    }
}