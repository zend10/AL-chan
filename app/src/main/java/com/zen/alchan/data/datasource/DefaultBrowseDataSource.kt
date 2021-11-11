package com.zen.alchan.data.datasource

import UserQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable
import type.UserStatisticsSort

class DefaultBrowseDataSource(private val apolloHandler: ApolloHandler) : BrowseDataSource {

    override fun getUserQuery(
        id: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserQuery.Data>> {
        val query = UserQuery(id = Input.fromNullable(id), sort = Input.optional(sort))
        return apolloHandler.apolloClient.rxQuery(query)
    }
}