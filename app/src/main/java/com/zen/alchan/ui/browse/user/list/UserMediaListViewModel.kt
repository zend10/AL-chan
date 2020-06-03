package com.zen.alchan.ui.browse.user.list

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.repository.OtherUserRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.enums.MediaListSort
import com.zen.alchan.helper.pojo.MediaFilteredData
import com.zen.alchan.helper.pojo.MediaListTabItem
import com.zen.alchan.helper.toMillis
import type.MediaType

class UserMediaListViewModel(private val otherUserRepository: OtherUserRepository,
                             private val userRepository: UserRepository,
                             val gson: Gson) : ViewModel() {

    var userId: Int? = null
    var mediaType: MediaType? = null
    var listType: ListType? = null

    var tabItemList = ArrayList<MediaListTabItem>()
    var selectedTab = 0

    var filterData: MediaFilteredData? = null

    var userData: UserMediaListCollectionQuery.User? = null

    var unfilteredData: UserMediaListCollectionQuery.Data? = null
    var sortedGroup = ArrayList<UserMediaListCollectionQuery.List?>()
    var currentList = ArrayList<UserMediaListCollectionQuery.Entry?>()

    val userMediaListCollection by lazy {
        otherUserRepository.userMediaListCollection
    }

    fun retrieveMediaListCollection() {
        if (userId == null || mediaType == null) {
            return
        }

        otherUserRepository.getUserMediaListCollection(userId!!, mediaType!!)
    }

    fun getTabItemArray(): Array<String> {
        val tabItemArrayList = ArrayList<String>()
        tabItemList.forEach { tabItemArrayList.add("${it.status} (${it.count})") }
        return tabItemArrayList.toTypedArray()
    }

    fun setSortedGroup() {
        val groupListWithSortedEntries = ArrayList<UserMediaListCollectionQuery.List?>()
        unfilteredData?.mediaListCollection?.lists?.forEach { list ->
            val sortedList = sortMediaListEntries(list?.entries)
            val filteredList = filterMediaListEntries(sortedList)
            groupListWithSortedEntries.add(
                UserMediaListCollectionQuery.List(
                    entries = filteredList,
                    name = list?.name,
                    isCustomList = list?.isCustomList,
                    isSplitCompletedList = list?.isSplitCompletedList,
                    status = list?.status
                )
            )
        }

        sortedGroup = sortMediaListGrouping(groupListWithSortedEntries)

        tabItemList.clear()
        sortedGroup.forEach { list -> tabItemList.add(MediaListTabItem(list?.name!!, list.entries?.size!!)) }

        if (tabItemList.isNullOrEmpty()) {
            Constant.DEFAULT_ANIME_LIST_ORDER.forEach { list -> tabItemList.add(MediaListTabItem(list, 0)) }
        }
    }

    private fun sortMediaListEntries(entries: List<UserMediaListCollectionQuery.Entry?>?): ArrayList<UserMediaListCollectionQuery.Entry?> {
        if (entries.isNullOrEmpty()) {
            return ArrayList()
        }

        var rowOrderEnum = when(userRepository.viewerData.value?.mediaListOptions?.rowOrder) {
            "title" -> MediaListSort.TITLE
            "score" -> MediaListSort.SCORE
            "updatedAt" -> MediaListSort.LAST_UPDATED
            "id" -> MediaListSort.LAST_ADDED
            else -> MediaListSort.TITLE
        }

        if (filterData != null && filterData?.selectedListSort != null) {
            rowOrderEnum = filterData?.selectedListSort!!
        }

        return when (rowOrderEnum) {
            MediaListSort.TITLE -> ArrayList(entries.sortedWith(compareBy { it?.media?.title?.userPreferred }))
            MediaListSort.SCORE -> ArrayList(entries.sortedWith(compareByDescending { it?.score }))
            MediaListSort.PROGRESS -> ArrayList(entries.sortedWith(compareByDescending { it?.progress }))
            MediaListSort.LAST_UPDATED -> ArrayList(entries.sortedWith(compareByDescending { it?.updatedAt }))
            MediaListSort.LAST_ADDED -> ArrayList(entries.sortedWith(compareByDescending { it?.id }))
            MediaListSort.START_DATE -> ArrayList(entries.sortedWith(compareByDescending { if (it?.startedAt != null) Converter.convertFuzzyDate(it.startedAt).toMillis() else it?.startedAt }))
            MediaListSort.COMPLETED_DATE -> ArrayList(entries.sortedWith(compareByDescending { if (it?.completedAt != null) Converter.convertFuzzyDate(it.completedAt).toMillis() else it?.completedAt }))
            MediaListSort.RELEASE_DATE -> ArrayList(entries.sortedWith(compareByDescending { if (it?.media?.startDate != null) Converter.convertFuzzyDate(it.media.startDate).toMillis() else it?.media?.startDate }))
            MediaListSort.AVERAGE_SCORE -> ArrayList(entries.sortedWith(compareByDescending { it?.media?.averageScore }))
            MediaListSort.POPULARITY -> ArrayList(entries.sortedWith(compareByDescending { it?.media?.popularity }))
            MediaListSort.PRIORITY -> ArrayList(entries.sortedWith(compareByDescending { it?.priority }))
        }
    }

    private fun filterMediaListEntries(entries: List<UserMediaListCollectionQuery.Entry?>?): ArrayList<UserMediaListCollectionQuery.Entry?> {
        if (entries.isNullOrEmpty()) {
            return ArrayList()
        }

        if (filterData == null) {
            return ArrayList(entries)
        }

        val filteredList = ArrayList<UserMediaListCollectionQuery.Entry?>()

        entries.forEach {
            if (filterData?.selectedFormat != null && it?.media?.format != filterData?.selectedFormat) {
                return@forEach
            }

            if (filterData?.selectedYear != null && it?.media?.seasonYear != filterData?.selectedYear) {
                return@forEach
            }

            if (filterData?.selectedSeason != null && it?.media?.season != filterData?.selectedSeason) {
                return@forEach
            }

            if (filterData?.selectedCountry != null && it?.media?.countryOfOrigin != filterData?.selectedCountry?.name) {
                return@forEach
            }

            if (filterData?.selectedStatus != null && it?.media?.status != filterData?.selectedStatus) {
                return@forEach
            }

            if (filterData?.selectedSource != null && it?.media?.source != filterData?.selectedSource) {
                return@forEach
            }

            if (!filterData?.selectedGenreList.isNullOrEmpty() && !it?.media?.genres.isNullOrEmpty() && !it?.media?.genres!!.containsAll(filterData?.selectedGenreList!!)) {
                return@forEach
            }

            filteredList.add(it)
        }

        return filteredList
    }

    private fun sortMediaListGrouping(mediaListGroup: List<UserMediaListCollectionQuery.List?>?): ArrayList<UserMediaListCollectionQuery.List?> {
        if (mediaListGroup.isNullOrEmpty()) {
            return ArrayList()
        }

        val sortedList = ArrayList<UserMediaListCollectionQuery.List?>()

        var sectionOrder: List<String?>? = null
        var customList: List<String?>? = null

        if (mediaType == MediaType.ANIME) {
            sectionOrder = userRepository.viewerData.value?.mediaListOptions?.animeList?.sectionOrder
            customList = userRepository.viewerData.value?.mediaListOptions?.animeList?.customLists
        } else if (mediaType == MediaType.MANGA) {
            sectionOrder = userRepository.viewerData.value?.mediaListOptions?.mangaList?.sectionOrder
            customList = userRepository.viewerData.value?.mediaListOptions?.mangaList?.customLists
        }

        sectionOrder?.forEach { section ->
            val groupList = mediaListGroup.find { group -> group?.name == section && group?.isCustomList == false }
            if (groupList != null) {
                sortedList.add(groupList)
            }
        }

        customList?.forEach { custom ->
            val groupList = mediaListGroup.find { group -> group?.name == custom && group?.isCustomList == true }
            if (groupList != null) {
                sortedList.add(groupList)
            }
        }

        return sortedList
    }

    fun getSelectedList(): ArrayList<UserMediaListCollectionQuery.Entry?> {
        val selectedList = sortedGroup.find { it?.name == tabItemList[selectedTab].status }?.entries

        return if (!selectedList.isNullOrEmpty()) {
            ArrayList(selectedList)
        } else {
            ArrayList()
        }
    }
}