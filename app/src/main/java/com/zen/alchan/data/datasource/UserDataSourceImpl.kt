package com.zen.alchan.data.datasource

import AniListSettingsMutation
import ListSettingsMutation
import ToggleFavouriteMutation
import ViewerQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.apollographql.apollo.rx2.rxPrefetch
import com.zen.alchan.data.network.ApolloHandler
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.helper.Constant
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import type.MediaListOptionsInput
import type.ScoreFormat
import type.UserTitleLanguage

class UserDataSourceImpl(private val apolloHandler: ApolloHandler) : UserDataSource {

    override fun getViewerData(): Observable<Response<ViewerQuery.Data>> {
        val query = ViewerQuery()
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
        val mutation = AniListSettingsMutation(
            titleLanguage = Input.fromNullable(titleLanguage),
            displayAdultContent = Input.fromNullable(adultContent),
            airingNotifications = Input.fromNullable(airingNotifications)
        )
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
        val animeSectionOrder = ArrayList<String?>()

        if (animeListOptions.splitCompletedSectionByFormat == true) {
            animeSectionOrder.addAll(Constant.DEFAULT_SPLIT_ANIME_LIST_ORDER)
        } else {
            animeSectionOrder.addAll(Constant.DEFAULT_ANIME_LIST_ORDER)
        }

        if (!animeListOptions.customLists.isNullOrEmpty()) {
            animeSectionOrder.addAll(ArrayList(animeListOptions.customLists))
        }

        val animeListOptionsBuilder = MediaListOptionsInput(
            sectionOrder = Input.fromNullable(animeSectionOrder),
            splitCompletedSectionByFormat = Input.fromNullable(animeListOptions.splitCompletedSectionByFormat),
            customLists = Input.fromNullable(animeListOptions.customLists),
            advancedScoring = Input.fromNullable(animeListOptions.advancedScoring),
            advancedScoringEnabled = Input.fromNullable(animeListOptions.advancedScoringEnabled)
        )

        val mangaSectionOrder = ArrayList<String?>()

        if (mangaListOptions.splitCompletedSectionByFormat == true) {
            mangaSectionOrder.addAll(Constant.DEFAULT_SPLIT_MANGA_LIST_ORDER)
        } else {
            mangaSectionOrder.addAll(Constant.DEFAULT_MANGA_LIST_ORDER)
        }

        if (!mangaListOptions.customLists.isNullOrEmpty()) {
            mangaSectionOrder.addAll(ArrayList(mangaListOptions.customLists))
        }

        val mangaListOptionsBuilder = MediaListOptionsInput(
            sectionOrder = Input.fromNullable(mangaSectionOrder),
            splitCompletedSectionByFormat = Input.fromNullable(mangaListOptions.splitCompletedSectionByFormat),
            customLists = Input.fromNullable(mangaListOptions.customLists),
            advancedScoring = Input.fromNullable(mangaListOptions.advancedScoring),
            advancedScoringEnabled = Input.fromNullable(mangaListOptions.advancedScoringEnabled)
        )

        val mutation = ListSettingsMutation(
            scoreFormat = Input.fromNullable(scoreFormat),
            rowOrder = Input.fromNullable(rowOrder),
            animeListOptions = Input.fromNullable(animeListOptionsBuilder),
            mangaListOptions = Input.fromNullable(mangaListOptionsBuilder)
        )
        val mutationCall = apolloHandler.apolloClient.mutate(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun toggleFavourite(
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
        val mutationCall = apolloHandler.apolloClient.prefetch(mutation)
        return Rx2Apollo.from(mutationCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}