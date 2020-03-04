package com.zen.alchan.data.datasource

import ViewerQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.helper.Constant
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
        val animeSectionOrder = ArrayList<String>()

        if (animeListOptions.splitCompletedSectionByFormat == true) {
            animeSectionOrder.addAll(Constant.DEFAULT_SPLIT_ANIME_LIST_ORDER)
        } else {
            animeSectionOrder.addAll(Constant.DEFAULT_ANIME_LIST_ORDER)
        }

        if (!animeListOptions.customLists.isNullOrEmpty()) {
            animeSectionOrder.addAll(ArrayList(animeListOptions.customLists))
        }

        val animeListOptionsBuilder = MediaListOptionsInput.builder()
            .sectionOrder(animeSectionOrder)
            .splitCompletedSectionByFormat(animeListOptions.splitCompletedSectionByFormat)
            .customLists(animeListOptions.customLists)
            .advancedScoring(animeListOptions.advancedScoring)
            .advancedScoringEnabled(animeListOptions.advancedScoringEnabled)
            .build()

        val mangaSectionOrder = ArrayList<String>()

        if (mangaListOptions.splitCompletedSectionByFormat == true) {
            mangaSectionOrder.addAll(Constant.DEFAULT_SPLIT_MANGA_LIST_ORDER)
        } else {
            mangaSectionOrder.addAll(Constant.DEFAULT_MANGA_LIST_ORDER)
        }

        if (!mangaListOptions.customLists.isNullOrEmpty()) {
            mangaSectionOrder.addAll(ArrayList(mangaListOptions.customLists))
        }

        val mangaListOptionsBuilder = MediaListOptionsInput.builder()
            .sectionOrder(mangaSectionOrder)
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