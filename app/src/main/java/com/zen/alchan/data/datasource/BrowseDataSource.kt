package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import type.UserStatisticsSort

interface BrowseDataSource {
    fun getUserQuery(id: Int, sort: List<UserStatisticsSort>): Observable<Response<UserQuery.Data>>
}