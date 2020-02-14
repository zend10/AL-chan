package com.zen.alchan.data.datasource

import ViewerQuery
import com.apollographql.apollo.api.Response
import io.reactivex.Observable

interface AuthDataSource {
    fun getViewerData(): Observable<Response<ViewerQuery.Data>>
}