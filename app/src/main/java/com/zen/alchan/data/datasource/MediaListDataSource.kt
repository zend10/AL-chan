package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import type.MediaType

interface MediaListDataSource {
    fun getMediaListCollectionQuery(userId: Int, mediaType: MediaType): Observable<Response<MediaListCollectionQuery.Data>>
    fun getMediaWithMediaListQuery(mediaId: Int, mediaType: MediaType): Observable<Response<MediaWithMediaListQuery.Data>>
}