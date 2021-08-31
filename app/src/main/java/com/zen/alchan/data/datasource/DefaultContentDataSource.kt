package com.zen.alchan.data.datasource

import GenreQuery
import HomeDataQuery
import TagQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable

class DefaultContentDataSource(private val apolloHandler: ApolloHandler) : ContentDataSource {

    override fun getHomeQuery(): Observable<Response<HomeDataQuery.Data>> {
        val query = HomeDataQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getGenres(): Observable<Response<GenreQuery.Data>> {
        val query = GenreQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getTags(): Observable<Response<TagQuery.Data>> {
        val query = TagQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }
}