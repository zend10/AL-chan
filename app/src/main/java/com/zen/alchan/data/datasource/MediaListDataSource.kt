package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import type.MediaListStatus

interface MediaListDataSource {
    fun getAnimeListData(userId: Int): Observable<Response<AnimeListCollectionQuery.Data>>

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
}