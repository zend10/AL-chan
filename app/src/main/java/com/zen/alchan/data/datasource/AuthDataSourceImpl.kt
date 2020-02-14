package com.zen.alchan.data.datasource

import ViewerQuery
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AuthDataSourceImpl(private val apolloHandler: ApolloHandler) : AuthDataSource {

    override fun getViewerData(): Observable<Response<ViewerQuery.Data>> {
        val query = ViewerQuery.builder().build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}