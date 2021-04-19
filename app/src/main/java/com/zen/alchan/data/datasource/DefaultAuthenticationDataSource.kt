package com.zen.alchan.data.datasource

import ViewerQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable

class DefaultAuthenticationDataSource(private val apolloHandler: ApolloHandler) : AuthenticationDataSource, BaseDataSource() {

    override fun getViewerQuery(): Observable<Response<ViewerQuery.Data>> {
        val query = ViewerQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }
}