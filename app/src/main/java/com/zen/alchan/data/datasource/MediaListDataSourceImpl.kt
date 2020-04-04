package com.zen.alchan.data.datasource

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
        val query = AnimeListCollectionQuery.builder().userId(userId).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getAnimeListDataDetail(id: Int, userId: Int): Observable<Response<AnimeListQuery.Data>> {
        val query = AnimeListQuery.builder().id(id).userId(userId).build()
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
        val mutation = AnimeListEntryMutation.builder()
            .id(entryId)
            .status(status)
            .progress(progress)
            .repeat(repeat)
            .build()
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
        val builder = AnimeListEntryMutation.builder()

        builder.id(entryId)
        builder.score(score)

        if (!advancedScores.isNullOrEmpty()) {
            builder.advancedScores(advancedScores)
        }

        val mutation = builder.build()

        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateAnimeList(
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
    ): Observable<Response<AnimeListEntryMutation.Data>> {
        val builder = AnimeListEntryMutation.builder()
        builder.id(entryId)
        builder.status(status)
        builder.score(score)
        builder.progress(progress)
        builder.repeat(repeat)
        builder.isPrivate(isPrivate)
        builder.notes(notes)
        builder.hiddenFromStatusLists(hiddenFromStatusLists)
        builder.customLists(customLists)

        if (!advancedScores.isNullOrEmpty()) {
            builder.advancedScores(advancedScores)
        }

        builder.startedAt(FuzzyDateInput.builder().year(startedAt?.year).month(startedAt?.month).day(startedAt?.day).build())
        builder.completedAt(FuzzyDateInput.builder().year(completedAt?.year).month(completedAt?.month).day(completedAt?.day).build())

        val mutation = builder.build()
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMangaListData(userId: Int): Observable<Response<MangaListCollectionQuery.Data>> {
        val query = MangaListCollectionQuery.builder().userId(userId).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMangaListDataDetail(
        id: Int,
        userId: Int
    ): Observable<Response<MangaListQuery.Data>> {
        val query = MangaListQuery.builder().id(id).userId(userId).build()
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
        val builder = MangaListEntryMutation.builder()
        builder.id(entryId)
        builder.status(status)
        builder.repeat(repeat)

        if (progress != null) {
            builder.progress(progress)
        }

        if (progressVolume != null) {
            builder.progressVolumes(progressVolume)
        }

        val mutation = builder.build()
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
        val builder = MangaListEntryMutation.builder()

        builder.id(entryId)
        builder.score(score)

        if (!advancedScores.isNullOrEmpty()) {
            builder.advancedScores(advancedScores)
        }

        val mutation = builder.build()

        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateMangaList(
        entryId: Int,
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
        completedAt: FuzzyDate?
    ): Observable<Response<MangaListEntryMutation.Data>> {
        val builder = MangaListEntryMutation.builder()
        builder.id(entryId)
        builder.status(status)
        builder.score(score)
        builder.progress(progress)
        builder.progressVolumes(progressVolume)
        builder.repeat(repeat)
        builder.isPrivate(isPrivate)
        builder.notes(notes)
        builder.hiddenFromStatusLists(hiddenFromStatusLists)
        builder.customLists(customLists)

        if (!advancedScores.isNullOrEmpty()) {
            builder.advancedScores(advancedScores)
        }

        builder.startedAt(FuzzyDateInput.builder().year(startedAt?.year).month(startedAt?.month).day(startedAt?.day).build())
        builder.completedAt(FuzzyDateInput.builder().year(completedAt?.year).month(completedAt?.month).day(completedAt?.day).build())

        val mutation = builder.build()
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteMediaList(entryId: Int): Observable<Response<DeleteMediaListEntryMutation.Data>> {
        val mutation = DeleteMediaListEntryMutation.builder().id(entryId).build()
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}