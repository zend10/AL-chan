package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.FuzzyDate
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.data.response.MediaListCollection
import com.zen.alchan.helper.pojo.MediaFilteredData
import type.MediaListStatus
import type.MediaType

interface MediaListRepository {
    val animeListDataResponse: LiveData<Resource<Boolean>>
    val animeListData: LiveData<MediaListCollection>
    val updateAnimeListEntryResponse: LiveData<Resource<Boolean>>

    val mangaListDataResponse: LiveData<Resource<Boolean>>
    val mangaListData: LiveData<MediaListCollection>
    val updateMangaListEntryResponse: LiveData<Resource<Boolean>>

    val mediaListDataDetailResponse: LiveData<Resource<MediaList>>
    val updateMediaListEntryDetailResponse: LiveData<Resource<Boolean>>
    val deleteMediaListEntryResponse: LiveData<Resource<Boolean>>

    var animeFilteredData: MediaFilteredData?
    var mangaFilteredData: MediaFilteredData?

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
    fun addAnimeList(
        mediaId: Int,
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

    fun retrieveMangaListData()
    fun retrieveMangaListDataDetail(entryId: Int)
    fun updateMangaProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        progress: Int?,
        progressVolumes: Int?
    )
    fun updateMangaScore(
        entryId: Int,
        score: Double,
        advancedScores: List<Double>?
    )
    fun updateMangaList(
        entryId: Int,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        progressVolumes: Int,
        repeat: Int,
        isPrivate: Boolean,
        notes: String?,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?
    )
    fun addMangaList(
        mediaId: Int,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        progressVolumes: Int,
        repeat: Int,
        isPrivate: Boolean,
        notes: String?,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?
    )

    fun handleNewFilter(newFilteredData: MediaFilteredData?, mediaType: MediaType)
    fun deleteMediaList(entryId: Int, mediaType: MediaType)
}