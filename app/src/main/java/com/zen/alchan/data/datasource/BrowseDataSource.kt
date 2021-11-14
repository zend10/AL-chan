package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import type.UserStatisticsSort

interface BrowseDataSource {
    fun getUserQuery(id: Int, sort: List<UserStatisticsSort>): Observable<Response<UserQuery.Data>>
    fun getMediaQuery(id: Int): Observable<Response<MediaQuery.Data>>
    fun getCharacterQuery(id: Int): Observable<Response<CharacterQuery.Data>>
}