package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.data.response.anilist.MediaListTypeOptions
import io.reactivex.Observable
import io.reactivex.Single
import type.ScoreFormat
import type.UserStaffNameLanguage
import type.UserStatisticsSort
import type.UserTitleLanguage

interface UserDataSource {
    fun getViewerQuery(): Observable<Response<ViewerQuery.Data>>
    fun getProfileQuery(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<ProfileDataQuery.Data>>
    fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        staffNameLanguage: UserStaffNameLanguage,
        activityMergeTime: Int,
        displayAdultContent: Boolean,
        airingNotifications: Boolean
    ): Single<Response<UpdateUserMutation.Data>>
    fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions
    ): Single<Response<UpdateUserMutation.Data>>
}