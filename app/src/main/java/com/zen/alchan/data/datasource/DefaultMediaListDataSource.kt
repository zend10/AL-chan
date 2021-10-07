package com.zen.alchan.data.datasource

import MediaListCollectionQuery
import MediaWithMediaListQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable
import type.MediaType

class DefaultMediaListDataSource(
    private val apolloHandler: ApolloHandler,
    private val statusVersion: Int,
    private val sourceVersion: Int
) : MediaListDataSource {

    override fun getMediaListCollectionQuery(
        userId: Int,
        mediaType: MediaType
    ): Observable<Response<MediaListCollectionQuery.Data>> {
        val query = MediaListCollectionQuery(
            Input.fromNullable(userId),
            Input.fromNullable(mediaType),
            Input.fromNullable(statusVersion),
            Input.fromNullable(sourceVersion)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getMediaWithMediaListQuery(
        mediaId: Int,
        mediaType: MediaType
    ): Observable<Response<MediaWithMediaListQuery.Data>> {
        val query = MediaWithMediaListQuery(
            Input.fromNullable(mediaId),
            Input.fromNullable(mediaType)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }
}