package com.zen.alchan.ui.animelist.editor

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

class AnimeListEditorViewModel(private val mediaListRepository: MediaListRepository,
                               private val userRepository: UserRepository,
                               val gson: Gson
) : ViewModel() {

    var isInit = false
    var entryId: Int? = null

    var mediaId: Int? = null
    var mediaTitle: String? = null
    var mediaEpisode: Int? = null

    var customListsList = ArrayList<CustomListsItem>()
    var advancedScoresList = ArrayList<AdvancedScoresItem>()

    var isFavourite: Boolean? = null
    var selectedStatus: MediaListStatus? = null
    var selectedScore: Double? = null
    var selectedAdvancedScores = ArrayList<Double>()
    var selectedProgress: Int? = null
    var selectedStartDate: FuzzyDate? = null
    var selectedFinishDate: FuzzyDate? = null
    var selectedRewatches: Int? = null
    var selectedNotes: String? = null
    var selectedCustomLists = ArrayList<String>()
    var selectedHidden: Boolean? = null
    var selectedPrivate: Boolean? = null

    val animeListDataDetailResponse by lazy {
        mediaListRepository.mediaListDataDetailResponse
    }

    val updateAnimeListEntryDetailResponse by lazy {
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

    val advancedScoringList: ArrayList<String>
        get() = if (userRepository.viewerData.value?.mediaListOptions?.animeList?.advancedScoringEnabled == true) {
            ArrayList(userRepository.viewerData.value?.mediaListOptions?.animeList?.advancedScoring!!)
        } else {
            ArrayList()
        }

    val savedCustomListsList: ArrayList<String>
        get() = if (userRepository.viewerData.value?.mediaListOptions?.animeList?.customLists.isNullOrEmpty()) {
            ArrayList()
        } else {
            ArrayList(userRepository.viewerData.value?.mediaListOptions?.animeList?.customLists!!)
        }

    val mediaListStatusList = listOf(
        MediaListStatus.CURRENT, MediaListStatus.REPEATING, MediaListStatus.COMPLETED, MediaListStatus.PAUSED, MediaListStatus.DROPPED, MediaListStatus.PLANNING
    )

    fun retrieveAnimeListDataDetail() {
        if (!isInit && entryId != null && entryId != 0) {
            mediaListRepository.retrieveAnimeListDataDetail(entryId!!)
        }
    }

    fun updateAnimeListEntryDetail() {
        if (
            selectedStatus != null &&
            selectedScore != null &&
            selectedProgress != null &&
            selectedRewatches != null &&
            selectedPrivate != null &&
            selectedHidden != null
        ) {
            if (entryId != null && entryId != 0) {
                mediaListRepository.updateAnimeList(
                    entryId!!,
                    selectedStatus!!,
                    selectedScore!!,
                    selectedProgress!!,
                    selectedRewatches!!,
                    selectedPrivate!!,
                    selectedNotes,
                    selectedHidden!!,
                    selectedCustomLists,
                    selectedAdvancedScores,
                    selectedStartDate,
                    selectedFinishDate
                )
            } else if (mediaId != null && mediaId != 0) {
                mediaListRepository.addAnimeList(
                    mediaId!!,
                    selectedStatus!!,
                    selectedScore!!,
                    selectedProgress!!,
                    selectedRewatches!!,
                    selectedPrivate!!,
                    selectedNotes,
                    selectedHidden!!,
                    selectedCustomLists,
                    selectedAdvancedScores,
                    selectedStartDate,
                    selectedFinishDate
                )
            }
        }
    }

    fun deleteAnimeListEntry() {
        if (entryId == null) return
        mediaListRepository.deleteMediaList(entryId!!, MediaType.ANIME)
    }

    fun updateFavourite() {
        userRepository.toggleFavourite(
            animeListDataDetailResponse.value?.data?.media?.id ?: mediaId, null, null, null, null
        )
    }
}