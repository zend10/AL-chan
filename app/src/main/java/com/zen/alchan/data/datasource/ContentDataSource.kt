package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable

interface ContentDataSource {
    fun getHomeQuery(): Observable<Response<HomeDataQuery.Data>>
}