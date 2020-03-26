package com.zen.alchan.data.datasource

import ViewerQuery
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.response.MediaListOptions
import com.zen.alchan.data.response.MediaListTypeOptions
import io.reactivex.Completable
import io.reactivex.Observable
import type.ScoreFormat
import type.UserTitleLanguage

interface UserDataSource {
    fun getViewerData(): Observable<Response<ViewerQuery.Data>>

    fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        adultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<Response<AniListSettingsMutation.Data>>

    fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions
    ): Observable<Response<ListSettingsMutation.Data>>

    fun toggleFavourite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?
    ): Completable
}