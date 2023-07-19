package com.zen.alchan.data.datasource

import com.apollographql.apollo3.api.ApolloResponse
import com.zen.alchan.MediaListCollectionQuery
import com.zen.alchan.MediaWithMediaListQuery
import com.zen.alchan.SaveMediaListEntryMutation
import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.type.MediaListStatus
import com.zen.alchan.type.MediaType
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface MediaListDataSource {
    fun getMediaListCollectionQuery(userId: Int, mediaType: MediaType): Observable<ApolloResponse<MediaListCollectionQuery.Data>>
    fun getMediaWithMediaListQuery(mediaId: Int): Observable<ApolloResponse<MediaWithMediaListQuery.Data>>
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
    ): Observable<ApolloResponse<SaveMediaListEntryMutation.Data>>
    fun deleteMediaListEntry(
        id: Int
    ): Completable
    fun updateMediaListScore(
        id: Int,
        score: Double,
        advancedScores: List<Double>?
    ): Observable<ApolloResponse<SaveMediaListEntryMutation.Data>>
    fun updateMediaListProgress(
        id: Int,
        status: MediaListStatus?,
        repeat: Int?,
        progress: Int?,
        progressVolumes: Int?
    ): Observable<ApolloResponse<SaveMediaListEntryMutation.Data>>
    fun updateMediaListStatus(
        mediaId: Int,
        status: MediaListStatus
    ): Observable<ApolloResponse<SaveMediaListEntryMutation.Data>>
}