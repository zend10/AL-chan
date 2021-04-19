package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable

interface AuthenticationDataSource {
    fun getViewerQuery(): Observable<Response<ViewerQuery.Data>>
}