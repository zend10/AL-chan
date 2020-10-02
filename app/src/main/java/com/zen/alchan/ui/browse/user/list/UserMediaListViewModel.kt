package com.zen.alchan.ui.browse.user.list

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.repository.AppSettingsRepository
import com.zen.alchan.data.repository.OtherUserRepository
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.enums.MediaListSort
import com.zen.alchan.helper.pojo.MediaFilterData
import com.zen.alchan.helper.pojo.MediaListTabItem
import com.zen.alchan.helper.toMillis
import type.MediaType

class UserMediaListViewModel(private val otherUserRepository: OtherUserRepository,
                             private val appSettingsRepository: AppSettingsRepository,
                             val gson: Gson) : ViewModel() {

    var userId: Int? = null
    var mediaType: MediaType? = null
    var listType: ListType? = null

    var tabItemList = ArrayList<MediaListTabItem>()
    var selectedTab = 0

    var filterData: MediaFilterData? = null

    var userData: UserMediaListCollectionQuery.User? = null

    var unfilteredData: UserMediaListCollectionQuery.Data? = null
    var sortedGroup = ArrayList<UserMediaListCollectionQuery.List?>()
    var currentList = ArrayList<UserMediaListCollectionQuery.Entry?>()

    val userMediaListCollection by lazy {
        otherUserRepository.userMediaListCollection
    }

    val useRelativeDate: Boolean
        get() = appSettingsRepository.appSettings.useRelativeDate == true

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

        var rowOrderEnum = when(userData?.mediaListOptions?.rowOrder) {
            "title" -> MediaListSort.TITLE
            "score" -> MediaListSort.SCORE
            "updatedAt" -> MediaListSort.LAST_UPDATED
            "id" -> MediaListSort.LAST_ADDED
            else -> MediaListSort.TITLE
        }

        if (filterData != null && filterData?.selectedMediaListSort != null) {
            rowOrderEnum = filterData?.selectedMediaListSort!!
        }

        val orderByDescending = filterData?.selectedMediaListOrderByDescending != false

        val sortedByTitle = entries.sortedWith(compareBy { it?.media?.title?.userPreferred })



        return when (rowOrderEnum) {
            MediaListSort.TITLE -> {
                val orderByDescendingSpecialCase = filterData?.selectedMediaListOrderByDescending == true
                ArrayList(if (orderByDescendingSpecialCase) sortedByTitle.reversed() else sortedByTitle)
            }
            MediaListSort.SCORE -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it?.score } else compareBy { it?.score }))
            MediaListSort.PROGRESS -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it?.progress } else compareBy { it?.progress }))
            MediaListSort.LAST_UPDATED -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it?.updatedAt } else compareBy { it?.updatedAt }))
            MediaListSort.LAST_ADDED -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it?.id } else compareBy { it?.id }))
            MediaListSort.START_DATE -> {
                ArrayList(sortedByTitle.sortedWith(if (orderByDescending) {
                    compareByDescending { if (it?.startedAt != null) Converter.convertFuzzyDate(it.startedAt).toMillis() else it?.startedAt }
                } else {
                    compareBy { if (it?.startedAt != null) Converter.convertFuzzyDate(it.startedAt).toMillis() else it?.startedAt }
                }))
            }
            MediaListSort.COMPLETED_DATE -> {
                ArrayList(sortedByTitle.sortedWith(if (orderByDescending) {
                    compareByDescending { if (it?.completedAt != null) Converter.convertFuzzyDate(it.completedAt).toMillis() else it?.completedAt }
                } else {
                    compareBy { if (it?.completedAt != null) Converter.convertFuzzyDate(it.completedAt).toMillis() else it?.completedAt }
                }))
            }
            MediaListSort.RELEASE_DATE -> {
                ArrayList(sortedByTitle.sortedWith(if (orderByDescending) {
                    compareByDescending { if (it?.media?.startDate != null) Converter.convertFuzzyDate(it.media.startDate).toMillis() else it?.media?.startDate }
                } else {
                    compareBy { if (it?.media?.startDate != null) Converter.convertFuzzyDate(it.media.startDate).toMillis() else it?.media?.startDate }
                }))
            }
            MediaListSort.AVERAGE_SCORE -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it?.media?.averageScore } else compareBy { it?.media?.averageScore }))
            MediaListSort.POPULARITY -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it?.media?.popularity } else compareBy { it?.media?.popularity }))
            MediaListSort.PRIORITY -> ArrayList(sortedByTitle.sortedWith(if (orderByDescending) compareByDescending { it?.priority } else compareBy { it?.priority }))
            MediaListSort.NEXT_AIRING -> {
                val orderByDescendingSpecialCase = filterData?.selectedMediaListOrderByDescending == true
                ArrayList(sortedByTitle.sortedWith(if (orderByDescendingSpecialCase) {
                    compareByDescending { it?.media?.nextAiringEpisode?.timeUntilAiring ?: Int.MIN_VALUE }
                } else {
                    compareBy { it?.media?.nextAiringEpisode?.timeUntilAiring ?: Int.MAX_VALUE }
                }))
            }
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

        entries.forEach entries@{
            if (!filterData?.selectedFormats.isNullOrEmpty() && filterData?.selectedFormats?.contains(it?.media?.format) != true) {
                return@entries
            }

            if (!filterData?.selectedStatuses.isNullOrEmpty() && filterData?.selectedStatuses?.contains(it?.media?.status) != true) {
                return@entries
            }

            if (!filterData?.selectedSources.isNullOrEmpty() &&  filterData?.selectedSources?.contains(it?.media?.source) != true) {
                return@entries
            }

            if (filterData?.selectedCountry != null && filterData?.selectedCountry?.name != it?.media?.countryOfOrigin) {
                return@entries
            }

            if (filterData?.selectedSeason != null && filterData?.selectedSeason != it?.media?.season) {
                return@entries
            }

            if (filterData?.selectedYear != null &&
                (it?.media?.startDate?.year == null ||
                filterData?.selectedYear?.minValue?.toString()?.substring(0..3)?.toInt()!! > it.media.startDate.year ||
                filterData?.selectedYear?.maxValue?.toString()?.substring(0..3)?.toInt()!! < it.media.startDate.year)
            ) {
                return@entries
            }

            if (!filterData?.selectedGenres.isNullOrEmpty() &&
                (it?.media?.genres.isNullOrEmpty() ||
                !it?.media?.genres!!.containsAll(filterData?.selectedGenres!!))
            ) {
                return@entries
            }

            if (!filterData?.selectedExcludedGenres.isNullOrEmpty() && !it?.media?.genres.isNullOrEmpty()) {
                filterData?.selectedExcludedGenres?.forEach { excludedGenre ->
                    if (it?.media?.genres?.contains(excludedGenre) == true) {
                        return@entries
                    }
                }
            }

            val mediaTagNames = it?.media?.tags?.filterNotNull()?.map { tag -> tag.name }

            if (!filterData?.selectedTagNames.isNullOrEmpty() &&
                (mediaTagNames.isNullOrEmpty() ||
                        !mediaTagNames.containsAll(filterData?.selectedTagNames!!))
            ) {
                return@entries
            }

            if (!filterData?.selectedExcludedTagNames.isNullOrEmpty() && !mediaTagNames.isNullOrEmpty()) {
                filterData?.selectedExcludedTagNames?.forEach { excludedTag ->
                    if (mediaTagNames.contains(excludedTag)) {
                        return@entries
                    }
                }
            }

            if (!filterData?.selectedLicensed.isNullOrEmpty()) {
                if (it?.media?.externalLinks.isNullOrEmpty()) {
                    return@entries
                }

                var hasSelectedLicense = false

                it?.media?.externalLinks?.forEach license@{ externalLink ->
                    if (filterData?.selectedLicensed?.contains(Constant.EXTERNAL_LINK_MAP[externalLink?.site]) == true) {
                        hasSelectedLicense = true
                        return@license
                    }
                }

                if (!hasSelectedLicense) {
                    return@entries
                }
            }

            if (filterData?.selectedEpisodes != null &&
                (it?.media?.episodes == null ||
                filterData?.selectedEpisodes?.minValue ?: 0 > it.media.episodes ||
                filterData?.selectedEpisodes?.maxValue ?: 0 < it.media.episodes)
            ) {
                return@entries
            }

            if (filterData?.selectedDuration != null &&
                (it?.media?.duration == null ||
                filterData?.selectedDuration?.minValue ?: 0 > it.media.duration ||
                filterData?.selectedDuration?.maxValue ?: 0 < it.media.duration)
            ) {
                return@entries
            }

            if (filterData?.selectedChapters != null &&
                (it?.media?.chapters == null ||
                filterData?.selectedChapters?.minValue ?: 0 > it.media.chapters ||
                filterData?.selectedChapters?.maxValue ?: 0 < it.media.chapters)
            ) {
                return@entries
            }

            if (filterData?.selectedVolumes != null &&
                (it?.media?.volumes == null ||
                filterData?.selectedVolumes?.minValue ?: 0 > it.media.volumes ||
                filterData?.selectedVolumes?.maxValue ?: 0 < it.media.volumes)
            ) {
                return@entries
            }

            if (filterData?.selectedAverageScore != null &&
                (it?.media?.averageScore == null ||
                filterData?.selectedAverageScore?.minValue ?: 0 > it.media.averageScore ||
                filterData?.selectedAverageScore?.maxValue ?: 0 < it.media.averageScore)
            ) {
                return@entries
            }

            if (filterData?.selectedPopularity != null &&
                (it?.media?.popularity == null ||
                filterData?.selectedPopularity?.minValue ?: 0 > it.media.popularity ||
                filterData?.selectedPopularity?.maxValue ?: 0 < it.media.popularity)
            ) {
                return@entries
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
        var defaultList: List<String?>? = null

        if (mediaType == MediaType.ANIME) {
            sectionOrder = userData?.mediaListOptions?.animeList?.sectionOrder
            customList = userData?.mediaListOptions?.animeList?.customLists
            defaultList = if (userData?.mediaListOptions?.animeList?.splitCompletedSectionByFormat == true) Constant.DEFAULT_SPLIT_ANIME_LIST_ORDER else Constant.DEFAULT_ANIME_LIST_ORDER
        } else if (mediaType == MediaType.MANGA) {
            sectionOrder = userData?.mediaListOptions?.mangaList?.sectionOrder
            customList = userData?.mediaListOptions?.mangaList?.customLists
            defaultList = if (userData?.mediaListOptions?.mangaList?.splitCompletedSectionByFormat == true) Constant.DEFAULT_SPLIT_MANGA_LIST_ORDER else Constant.DEFAULT_MANGA_LIST_ORDER
        }

        sectionOrder?.forEach { section ->
            val groupList = mediaListGroup.find { group -> group?.name == section }
            if (groupList != null) {
                sortedList.add(groupList)
            }
        }

        customList?.forEach { custom ->
            val groupList = mediaListGroup.find { group -> group?.name == custom && group?.isCustomList == true }
            if (groupList != null && !sortedList.contains(groupList)) {
                sortedList.add(groupList)
            }
        }

        defaultList?.forEach { default ->
            val groupList = mediaListGroup.find { group -> group?.name == default && group?.isCustomList == false }
            if (groupList != null && !sortedList.contains(groupList)) {
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