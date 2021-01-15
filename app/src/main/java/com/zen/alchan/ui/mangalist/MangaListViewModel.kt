package com.zen.alchan.ui.mangalist

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.ListStyleRepository
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.helper.pojo.MediaFilterData
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

    val filterData: MediaFilterData?
        get() = mediaListRepository.mangaFilterData

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
        get() = userRepository.currentUser?.options?.displayAdultContent ?: false

    val scoreFormat: ScoreFormat
        get() = userRepository.currentUser?.mediaListOptions?.scoreFormat ?: ScoreFormat.POINT_100

    val advancedScoringEnabled: Boolean
        get() = userRepository.currentUser?.mediaListOptions?.mangaList?.advancedScoringEnabled == true

    val advancedScoringList: ArrayList<String?>
        get() = if (userRepository.currentUser?.mediaListOptions?.mangaList?.advancedScoringEnabled == true) {
            ArrayList(userRepository.currentUser?.mediaListOptions?.mangaList?.advancedScoring!!)
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

    fun setFilteredData(newFilterData: MediaFilterData?) {
        mediaListRepository.handleNewFilter(newFilterData, MediaType.MANGA)
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
        val selectedList = ArrayList<MediaList>()
        if (selectedTab == 0) {
            tabItemList.forEachIndexed { index, mediaListTabItem ->
                if (index != 0) {
                    val checkList = ArrayList(mangaListData.value?.lists?.find { list -> list.name == mediaListTabItem.status }?.entries ?: listOf())
                    if (!checkList.isNullOrEmpty()) {
                        selectedList.add(MediaList(id = 0, notes = mediaListTabItem.status, progress = mediaListTabItem.count, status = null, score = null, progressVolumes = null, repeat = null, priority = null, private = null, hiddenFromStatusList = null, customLists = null, advancedScores = null, startedAt = null, completedAt = null, updatedAt = null, createdAt = null, media = null))
                        selectedList.addAll(checkList)
                    }
                }
            }
        } else {
            selectedList.addAll(mangaListData.value?.lists?.find { it.name == tabItemList[selectedTab].status }?.entries ?: listOf())
        }

        return if (!selectedList.isNullOrEmpty()) {
            ArrayList(selectedList)
        } else {
            ArrayList()
        }
    }
}