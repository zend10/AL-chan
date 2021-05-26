package com.zen.alchan.data.datasource

import MediaListCollectionQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable
import type.MediaType

class DefaultMediaListDataSource(private val apolloHandler: ApolloHandler) : MediaListDataSource {

    override fun getMediaListCollectionQuery(
        userId: Int,
        mediaType: MediaType
    ): Observable<Response<MediaListCollectionQuery.Data>> {
        val query = MediaListCollectionQuery(
            Input.fromNullable(userId),
            Input.fromNullable(mediaType),
            Input.fromNullable(apolloHandler.apiVersion)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }
}