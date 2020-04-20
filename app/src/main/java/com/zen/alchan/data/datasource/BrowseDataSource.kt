package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import io.reactivex.Observer

interface BrowseDataSource {
    fun getCharacter(id: Int): Observable<Response<CharacterQuery.Data>>
    fun getCharacterMedia(id: Int, page: Int): Observable<Response<CharacterMediaConnectionQuery.Data>>
    fun checkCharacterIsFavorite(id: Int): Observable<Response<CharacterIsFavoriteQuery.Data>>
}