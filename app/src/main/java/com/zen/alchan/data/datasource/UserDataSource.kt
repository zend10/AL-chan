package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import io.reactivex.Single
import type.UserStatisticsSort
import type.UserTitleLanguage

interface UserDataSource {
    fun getViewerQuery(): Observable<Response<ViewerQuery.Data>>
    fun getProfileQuery(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<ProfileDataQuery.Data>>
    fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        activityMergeTime: Int,
        displayAdultContent: Boolean,
        airingNotifications: Boolean
    ): Single<Response<UpdateUserMutation.Data>>
}