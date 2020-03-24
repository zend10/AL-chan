package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaListCollection
import com.zen.alchan.helper.pojo.MediaFilteredData
import type.MediaListStatus

interface MediaListRepository {
    val animeListDataResponse: LiveData<Resource<Boolean>>
    val animeListData: LiveData<MediaListCollection>

    val updateAnimeListEntryResponse: LiveData<Resource<Boolean>>

    var filteredData: MediaFilteredData?

    fun retrieveAnimeListData()
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
    fun handleNewFilter(newFilteredData: MediaFilteredData?)
}