package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.FuzzyDate
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.data.response.MediaListCollection
import com.zen.alchan.helper.pojo.MediaFilteredData
import type.MediaListStatus

interface MediaListRepository {
    val animeListDataResponse: LiveData<Resource<Boolean>>
    val animeListDataDetailResponse: LiveData<Resource<MediaList>>

    val animeListData: LiveData<MediaListCollection>

    val updateAnimeListEntryResponse: LiveData<Resource<Boolean>>
    val updateAnimeListEntryDetailResponse: LiveData<Resource<Boolean>>

    val deleteMediaListEntryResponse: LiveData<Resource<Boolean>>

    var filteredData: MediaFilteredData?

    fun retrieveAnimeListData()
    fun retrieveAnimeListDataDetail(entryId: Int)
    fun updateAnimeProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        progress: Int
    )
    fun updateAnimeScore(
        entryId: Int,
        score: Double,
        advancedScores: List<Double>?
    )
    fun updateAnimeList(
        entryId: Int,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        repeat: Int,
        isPrivate: Boolean,
        notes: String?,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?
    )
    fun handleNewFilter(newFilteredData: MediaFilteredData?)
    fun deleteMediaList(entryId: Int)
}