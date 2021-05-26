package com.zen.alchan.data.datasource

import ProfileDataQuery
import ViewerQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable
import type.UserStatisticsSort

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
}