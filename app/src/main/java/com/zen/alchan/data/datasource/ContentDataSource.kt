package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable

interface ContentDataSource {
    fun getHomeQuery(): Observable<Response<HomeDataQuery.Data>>
    fun getGenres(): Observable<Response<GenreQuery.Data>>
    fun getTags(): Observable<Response<TagQuery.Data>>
}