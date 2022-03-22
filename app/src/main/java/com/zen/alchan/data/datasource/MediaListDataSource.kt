package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.zen.alchan.data.response.anilist.FuzzyDate
import io.reactivex.Completable
import io.reactivex.Observable
import type.MediaListStatus
import type.MediaType

interface MediaListDataSource {
    fun getMediaListCollectionQuery(userId: Int, mediaType: MediaType): Observable<Response<MediaListCollectionQuery.Data>>
    fun getMediaWithMediaListQuery(mediaId: Int): Observable<Response<MediaWithMediaListQuery.Data>>
    fun toggleFavorite(animeId: Int?, mangaId: Int?, characterId: Int?, staffId: Int?, studioId: Int?): Completable
    fun updateMediaListEntry(
        id: Int?,
        mediaId: Int?,
        status: MediaListStatus,
        score: Double,
        progress: Int,
        progressVolumes: Int?,
        repeat: Int,
        priority: Int,
        isPrivate: Boolean,
        notes: String,
        hiddenFromStatusLists: Boolean,
        customLists: List<String>?,
        advancedScores: List<Double>?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?
    ): Observable<Response<SaveMediaListEntryMutation.Data>>
    fun deleteMediaListEntry(
        id: Int
    ): Completable
    fun updateMediaListScore(
        id: Int,
        score: Double,
        advancedScores: List<Double>?
    ): Observable<Response<SaveMediaListEntryMutation.Data>>
    fun updateMediaListProgress(
        id: Int,
        status: MediaListStatus?,
        repeat: Int?,
        progress: Int?,
        progressVolumes: Int?
    ): Observable<Response<SaveMediaListEntryMutation.Data>>
}