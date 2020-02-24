package com.zen.alchan.data.datasource

import ViewerQuery
import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import type.UserTitleLanguage

interface UserDataSource {
    fun getViewerData(): Observable<Response<ViewerQuery.Data>>
    fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        adultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<Response<AniListSettingsMutation.Data>>
}