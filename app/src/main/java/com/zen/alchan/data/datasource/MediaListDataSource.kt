package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable

interface MediaListDataSource {
    fun getAnimeListData(userId: Int): Observable<Response<AnimeListCollectionQuery.Data>>
}