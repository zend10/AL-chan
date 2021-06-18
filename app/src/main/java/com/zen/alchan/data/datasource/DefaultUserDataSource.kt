package com.zen.alchan.data.datasource

import ProfileDataQuery
import UpdateUserMutation
import ViewerQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxMutate
import com.apollographql.apollo.rx2.rxPrefetch
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import type.UserStaffNameLanguage
import type.UserStatisticsSort
import type.UserTitleLanguage

class DefaultUserDataSource(private val apolloHandler: ApolloHandler) : UserDataSource {

    override fun getViewerQuery(): Observable<Response<ViewerQuery.Data>> {
        val query = ViewerQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getProfileQuery(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<ProfileDataQuery.Data>> {
        val query = ProfileDataQuery(userId = userId, sort = Input.optional(sort))
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        staffNameLanguage: UserStaffNameLanguage,
        activityMergeTime: Int,
        displayAdultContent: Boolean,
        airingNotifications: Boolean
    ): Single<Response<UpdateUserMutation.Data>> {
        val mutation = UpdateUserMutation(
            titleLanguage = Input.fromNullable(titleLanguage),
            staffNameLanguage = Input.fromNullable(staffNameLanguage),
            activityMergeTime = Input.fromNullable(activityMergeTime),
            displayAdultContent = Input.fromNullable(displayAdultContent),
            airingNotifications = Input.fromNullable(airingNotifications)
        )
        return apolloHandler.apolloClient.rxMutate(mutation)
    }
}