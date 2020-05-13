package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.zen.alchan.data.response.FuzzyDate
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import type.MediaListStatus

interface MediaListDataSource {
    fun getAnimeListData(userId: Int): Observable<Response<AnimeListCollectionQuery.Data>>

    fun getAnimeListDataDetail(id: Int, userId: Int): Observable<Response<AnimeListQuery.Data>>

    fun updateAnimeProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        progress: Int
    ): Observable<Response<AnimeListEntryMutation.Data>>

    fun updateAnimeScore(
        entryId: Int,
        score: Double,
        advancedScores: List<Double>?
    ): Observable<Response<AnimeListEntryMutation.Data>>

    fun updateAnimeList(
        entryId: Int?,
        mediaId: Int?,
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
        completedAt: FuzzyDate?,
        priority: Int?
    ): Observable<Response<AnimeListEntryMutation.Data>>

    fun getMangaListData(userId: Int): Observable<Response<MangaListCollectionQuery.Data>>

    fun getMangaListDataDetail(id: Int, userId: Int): Observable<Response<MangaListQuery.Data>>

    fun updateMangaProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        progress: Int?,
        progressVolume: Int?
    ): Observable<Response<MangaListEntryMutation.Data>>

    fun updateMangaScore(
        entryId: Int,
        score: Double,
        advancedScores: List<Double>?
    ): Observable<Response<MangaListEntryMutation.Data>>

    fun updateMangaList(
        entryId: Int?,
        mediaId: Int?,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        progressVolume: Int,
        repeat: Int,
        isPrivate: Boolean,
        notes: String?,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?,
        priority: Int?
    ): Observable<Response<MangaListEntryMutation.Data>>

    fun deleteMediaList(
        entryId: Int
    ): Observable<Response<DeleteMediaListEntryMutation.Data>>

    fun addAnimeToPlanning(mediaId: Int): Observable<Response<AnimeListEntryMutation.Data>>
}