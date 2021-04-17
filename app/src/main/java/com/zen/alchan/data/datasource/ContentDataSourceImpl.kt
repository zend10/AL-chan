package com.zen.alchan.data.datasource

import HomeDataQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable

class ContentDataSourceImpl(private val apolloHandler: ApolloHandler) : BaseDataSource(), ContentDataSource {

    override fun getHomeQuery(): Observable<Response<HomeDataQuery.Data>> {
        val query = HomeDataQuery()
        return apolloHandler.apolloClient.rxQuery(query)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .onErrorResumeNext(::handleApolloError)
    }
}