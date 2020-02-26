package com.zen.alchan.data.datasource

import ViewerQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import com.zen.alchan.data.response.MediaListTypeOptions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import type.MediaListOptionsInput
import type.ScoreFormat
import type.UserTitleLanguage

class UserDataSourceImpl(private val apolloHandler: ApolloHandler) : UserDataSource {

    override fun getViewerData(): Observable<Response<ViewerQuery.Data>> {
        val query = ViewerQuery.builder().build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        adultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<Response<AniListSettingsMutation.Data>> {
        val mutation = AniListSettingsMutation.builder()
            .titleLanguage(titleLanguage)
            .displayAdultContent(adultContent)
            .airingNotifications(airingNotifications)
            .build()
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions
    ): Observable<Response<ListSettingsMutation.Data>> {
        val animeListOptionsBuilder = MediaListOptionsInput.builder()
            .splitCompletedSectionByFormat(animeListOptions.splitCompletedSectionByFormat)
            .customLists(animeListOptions.customLists)
            .advancedScoring(animeListOptions.advancedScoring)
            .advancedScoringEnabled(animeListOptions.advancedScoringEnabled)
            .build()

        val mangaListOptionsBuilder = MediaListOptionsInput.builder()
            .splitCompletedSectionByFormat(mangaListOptions.splitCompletedSectionByFormat)
            .customLists(mangaListOptions.customLists)
            .advancedScoring(mangaListOptions.advancedScoring)
            .advancedScoringEnabled(mangaListOptions.advancedScoringEnabled)
            .build()

        val mutation = ListSettingsMutation.builder()
            .scoreFormat(scoreFormat)
            .rowOrder(rowOrder)
            .animeListOptions(animeListOptionsBuilder)
            .mangaListOptions(mangaListOptionsBuilder)
            .build()

        val mutationCall = apolloHandler.apolloClient.mutate(mutation)

        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}