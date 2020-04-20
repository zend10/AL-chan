package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BrowseDataSourceImpl(private val apolloHandler: ApolloHandler) : BrowseDataSource {

    override fun getCharacter(id: Int): Observable<Response<CharacterQuery.Data>> {
        val query = CharacterQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getCharacterMedia(
        id: Int,
        page: Int
    ): Observable<Response<CharacterMediaConnectionQuery.Data>> {
        val query = CharacterMediaConnectionQuery.builder().id(id).page(page).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkCharacterIsFavorite(id: Int): Observable<Response<CharacterIsFavoriteQuery.Data>> {
        val query = CharacterIsFavoriteQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}