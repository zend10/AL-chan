package com.zen.alchan.ui.mangalist.editor

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.MediaListRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.data.response.FuzzyDate
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.pojo.AdvancedScoresItem
import com.zen.alchan.helper.pojo.CustomListsItem
import type.MediaListStatus
import type.MediaType
import type.ScoreFormat

class MangaListEditorViewModel(private val mediaListRepository: MediaListRepository,
                               private val userRepository: UserRepository,
                               val gson: Gson
) : ViewModel() {

    var isInit = false
    var entryId: Int? = null

    var mediaId: Int? = null
    var mediaTitle: String? = null
    var mediaChapter: Int? = null
    var mediaVolume: Int? = null

    var customListsList = ArrayList<CustomListsItem>()
    var advancedScoresList = ArrayList<AdvancedScoresItem>()

    var isFavourite: Boolean? = null
    var selectedStatus: MediaListStatus? = null
    var selectedScore: Double? = null
    var selectedAdvancedScores = ArrayList<Double>()
    var selectedProgress: Int? = null
    var selectedProgressVolumes: Int? = null
    var selectedStartDate: FuzzyDate? = null
    var selectedFinishDate: FuzzyDate? = null
    var selectedRewatches: Int? = null
    var selectedNotes: String? = null
    var selectedCustomLists = ArrayList<String>()
    var selectedHidden: Boolean? = null
    var selectedPrivate: Boolean? = null
    var selectedPriority: Int? = null

    var isCustomListsModified = false

    val mangaListDataDetailResponse by lazy {
        mediaListRepository.mediaListDataDetailResponse
    }

    val updateMangaListEntryDetailResponse by lazy {
        mediaListRepository.updateMediaListEntryDetailResponse
    }

    val deleteMediaListEntryResponse by lazy {
        mediaListRepository.deleteMediaListEntryResponse
    }

    val toggleFavouriteResponse by lazy {
        userRepository.toggleFavouriteResponse
    }

    val viewerData: User?
        get() = userRepository.viewerData.value

    val scoreFormat: ScoreFormat
        get() = userRepository.viewerData.value?.mediaListOptions?.scoreFormat ?: ScoreFormat.POINT_100

    val advancedScoringList: ArrayList<String?>
        get() = if (userRepository.viewerData.value?.mediaListOptions?.mangaList?.advancedScoringEnabled == true) {
            ArrayList(userRepository.viewerData.value?.mediaListOptions?.mangaList?.advancedScoring!!)
        } else {
            ArrayList()
        }

    val savedCustomListsList: ArrayList<String?>
        get() = if (userRepository.viewerData.value?.mediaListOptions?.mangaList?.customLists.isNullOrEmpty()) {
            ArrayList()
        } else {
            ArrayList(userRepository.viewerData.value?.mediaListOptions?.mangaList?.customLists!!)
        }

    val mediaListStatusList = listOf(
        MediaListStatus.CURRENT, MediaListStatus.REPEATING, MediaListStatus.COMPLETED, MediaListStatus.PAUSED, MediaListStatus.DROPPED, MediaListStatus.PLANNING
    )

    fun retrieveMangaListDataDetail() {
        if (!isInit && entryId != null && entryId != 0) {
            mediaListRepository.retrieveMangaListDataDetail(entryId!!)
        }
    }

    fun updateMangaListEntryDetail() {
        if (
            selectedStatus != null &&
            selectedScore != null &&
            selectedProgress != null &&
            selectedProgressVolumes != null &&
            selectedRewatches != null &&
            selectedPrivate != null &&
            selectedHidden != null
        ) {
            if (entryId != null && entryId != 0) {
                mediaListRepository.updateMangaList(
                    entryId!!,
                    selectedStatus!!,
                    selectedScore!!,
                    selectedProgress!!,
                    selectedProgressVolumes!!,
                    selectedRewatches!!,
                    selectedPrivate!!,
                    selectedNotes,
                    selectedHidden!!,
                    selectedCustomLists,
                    selectedAdvancedScores,
                    selectedStartDate,
                    selectedFinishDate,
                    selectedPriority,
                    isCustomListsModified
                )
            } else if (mediaId != null && mediaId != 0) {
                mediaListRepository.addMangaList(
                    mediaId!!,
                    selectedStatus!!,
                    selectedScore!!,
                    selectedProgress!!,
                    selectedProgressVolumes!!,
                    selectedRewatches!!,
                    selectedPrivate!!,
                    selectedNotes,
                    selectedHidden!!,
                    selectedCustomLists,
                    selectedAdvancedScores,
                    selectedStartDate,
                    selectedFinishDate,
                    selectedPriority
                )
            }
        }
    }

    fun deleteMangaListEntry() {
        if (entryId == null) return
        mediaListRepository.deleteMediaList(entryId!!, MediaType.MANGA)
    }

    fun updateFavourite() {
        userRepository.toggleFavourite(
            null, mangaListDataDetailResponse.value?.data?.media?.id ?: mediaId, null, null, null
        )
    }

    fun getPriorityLabel(): String {
        return when (selectedPriority) {
            0 -> "No Priority"
            1 -> "Very Low"
            2 -> "Low"
            3 -> "Medium"
            4 -> "High"
            5 -> "Very High"
            else -> "No Priority"
        }
    }
}