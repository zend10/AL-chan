package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
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
}