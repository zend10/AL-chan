package com.zen.alchan.data.datasource

import DeleteMediaListEntryMutation
import MediaListCollectionQuery
import MediaWithMediaListQuery
import SaveMediaListEntryMutation
import ToggleFavouriteMutation
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.apollographql.apollo.rx2.rxMutate
import com.apollographql.apollo.rx2.rxPrefetch
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import com.zen.alchan.data.response.anilist.FuzzyDate
import io.reactivex.Completable
import io.reactivex.Observable
import type.FuzzyDateInput
import type.MediaListStatus
import type.MediaType

class DefaultMediaListDataSource(
    private val apolloHandler: ApolloHandler,
    private val statusVersion: Int,
    private val sourceVersion: Int
) : MediaListDataSource {

    override fun getMediaListCollectionQuery(
        userId: Int,
        mediaType: MediaType
    ): Observable<Response<MediaListCollectionQuery.Data>> {
        val query = MediaListCollectionQuery(
            Input.fromNullable(userId),
            Input.fromNullable(mediaType),
            Input.fromNullable(statusVersion),
            Input.fromNullable(sourceVersion)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getMediaWithMediaListQuery(
        mediaId: Int,
        mediaType: MediaType
    ): Observable<Response<MediaWithMediaListQuery.Data>> {
        val query = MediaWithMediaListQuery(
            Input.fromNullable(mediaId),
            Input.fromNullable(mediaType)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun toggleFavorite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?
    ): Completable {
        val mutation = ToggleFavouriteMutation(
            animeId = Input.optional(animeId),
            mangaId = Input.optional(mangaId),
            characterId = Input.optional(characterId),
            staffId = Input.optional(staffId),
            studioId = Input.optional(studioId)
        )
        return apolloHandler.apolloClient.rxPrefetch(mutation)
    }

    override fun updateMediaListEntry(
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
    ): Observable<Response<SaveMediaListEntryMutation.Data>> {
        val mutation = SaveMediaListEntryMutation(
            id = Input.optional(id),
            mediaId = Input.optional(mediaId),
            status = Input.fromNullable(status),
            score = Input.fromNullable(score),
            progress = Input.fromNullable(progress),
            progressVolumes = Input.fromNullable(progressVolumes),
            repeat = Input.fromNullable(repeat),
            priority = Input.fromNullable(priority),
            isPrivate = Input.fromNullable(isPrivate),
            notes = Input.fromNullable(notes),
            hiddenFromStatusLists = Input.fromNullable(hiddenFromStatusLists),
            customLists = Input.fromNullable(customLists),
            advancedScores = Input.fromNullable(advancedScores),
            startedAt = Input.fromNullable(
                FuzzyDateInput(
                    year = Input.fromNullable(startedAt?.year),
                    month = Input.fromNullable(startedAt?.month),
                    day = Input.fromNullable(startedAt?.day)
                )
            ),
            completedAt = Input.fromNullable(
                FuzzyDateInput(
                    year = Input.fromNullable(completedAt?.year),
                    month = Input.fromNullable(completedAt?.month),
                    day = Input.fromNullable(completedAt?.day)
                )
            )
        )
        return Rx2Apollo.from(apolloHandler.apolloClient.mutate(mutation))
    }

    override fun deleteMediaListEntry(id: Int): Completable {
        val mutation = DeleteMediaListEntryMutation(Input.fromNullable(id))
        return apolloHandler.apolloClient.rxPrefetch(mutation)
    }

    override fun updateMediaListScore(
        id: Int,
        score: Double,
        advancedScores: List<Double>?
    ): Observable<Response<SaveMediaListEntryMutation.Data>> {
        val mutation = SaveMediaListEntryMutation(
            id = Input.fromNullable(id),
            score = Input.fromNullable(score),
            advancedScores = Input.optional(advancedScores)
        )
        return Rx2Apollo.from(apolloHandler.apolloClient.mutate(mutation))
    }
}