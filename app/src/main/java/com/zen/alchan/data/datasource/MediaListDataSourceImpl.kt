package com.zen.alchan.data.datasource

import AnimeListCollectionQuery
import AnimeListEntryMutation
import AnimeListQuery
import DeleteMediaListEntryMutation
import MangaListCollectionQuery
import MangaListEntryMutation
import MangaListQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.apollographql.apollo.rx2.rxMutate
import com.zen.alchan.data.network.ApolloHandler
import com.zen.alchan.data.response.FuzzyDate
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import type.FuzzyDateInput
import type.MediaListStatus
import java.util.*

class MediaListDataSourceImpl(private val apolloHandler: ApolloHandler) : MediaListDataSource {

    override fun getAnimeListData(userId: Int): Observable<Response<AnimeListCollectionQuery.Data>> {
        val query = AnimeListCollectionQuery(userId = Input.fromNullable(userId))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getAnimeListDataDetail(id: Int, userId: Int): Observable<Response<AnimeListQuery.Data>> {
        val query = AnimeListQuery(id = Input.fromNullable(id), userId = Input.fromNullable(userId))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateAnimeProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        progress: Int
    ): Observable<Response<AnimeListEntryMutation.Data>> {
        val mutation = AnimeListEntryMutation(
            id = Input.fromNullable(entryId),
            status = Input.fromNullable(status),
            progress = Input.fromNullable(progress),
            repeat = Input.fromNullable(repeat)
        )
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateAnimeScore(
        entryId: Int,
        score: Double,
        advancedScores: List<Double>?
    ): Observable<Response<AnimeListEntryMutation.Data>> {
        val checkAdvancedScores = if (advancedScores.isNullOrEmpty()) null else advancedScores
        val mutation = AnimeListEntryMutation(
            id = Input.fromNullable(entryId),
            score = Input.fromNullable(score),
            advancedScores = Input.optional(checkAdvancedScores)
        )
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateAnimeList(
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
    ): Observable<Response<AnimeListEntryMutation.Data>> {
        val checkAdvancedScores = if (advancedScores.isNullOrEmpty()) null else advancedScores
        val mutation = AnimeListEntryMutation(
            id = Input.optional(entryId),
            mediaId = Input.optional(mediaId),
            status = Input.fromNullable(status),
            score = Input.fromNullable(score),
            progress = Input.fromNullable(progress),
            repeat = Input.fromNullable(repeat),
            isPrivate = Input.fromNullable(isPrivate),
            notes = Input.fromNullable(notes),
            hiddenFromStatusLists = Input.fromNullable(hiddenFromStatusLists),
            customLists = Input.fromNullable(customLists),
            priority = Input.optional(priority),
            advancedScores = Input.optional(checkAdvancedScores),
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
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMangaListData(userId: Int): Observable<Response<MangaListCollectionQuery.Data>> {
        val query = MangaListCollectionQuery(userId = Input.fromNullable(userId))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMangaListDataDetail(
        id: Int,
        userId: Int
    ): Observable<Response<MangaListQuery.Data>> {
        val query = MangaListQuery(id = Input.fromNullable(id), userId = Input.fromNullable(userId))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateMangaProgress(
        entryId: Int,
        status: MediaListStatus,
        repeat: Int,
        progress: Int?,
        progressVolume: Int?
    ): Observable<Response<MangaListEntryMutation.Data>> {
        val mutation = MangaListEntryMutation(
            id = Input.fromNullable(entryId),
            status = Input.fromNullable(status),
            repeat = Input.fromNullable(repeat),
            progress = Input.optional(progress),
            progressVolumes = Input.optional(progressVolume)
        )
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateMangaScore(
        entryId: Int,
        score: Double,
        advancedScores: List<Double>?
    ): Observable<Response<MangaListEntryMutation.Data>> {
        val checkAdvancedScores = if (advancedScores.isNullOrEmpty()) null else advancedScores
        val mutation = MangaListEntryMutation(
            id = Input.fromNullable(entryId),
            score = Input.fromNullable(score),
            advancedScores = Input.optional(checkAdvancedScores)
        )
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateMangaList(
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
    ): Observable<Response<MangaListEntryMutation.Data>> {
        val checkAdvancedScores = if (advancedScores.isNullOrEmpty()) null else advancedScores
        val mutation = MangaListEntryMutation(
            id = Input.optional(entryId),
            mediaId = Input.optional(mediaId),
            status = Input.fromNullable(status),
            score = Input.fromNullable(score),
            progress = Input.fromNullable(progress),
            progressVolumes = Input.fromNullable(progressVolume),
            repeat = Input.fromNullable(repeat),
            isPrivate = Input.fromNullable(isPrivate),
            notes = Input.fromNullable(notes),
            hiddenFromStatusLists = Input.fromNullable(hiddenFromStatusLists),
            customLists = Input.fromNullable(customLists),
            priority = Input.optional(priority),
            advancedScores = Input.optional(checkAdvancedScores),
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
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteMediaList(entryId: Int): Observable<Response<DeleteMediaListEntryMutation.Data>> {
        val mutation = DeleteMediaListEntryMutation(id = Input.fromNullable(entryId))
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun addAnimeToPlanning(mediaId: Int): Observable<Response<AnimeListEntryMutation.Data>> {
        val mutation = AnimeListEntryMutation(
            mediaId = Input.fromNullable(mediaId),
            status = Input.fromNullable(MediaListStatus.PLANNING)
        )
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}