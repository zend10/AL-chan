package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchDataSourceImpl(private val apolloHandler: ApolloHandler) : SearchDataSource {

    override fun searchAnime(
        page: Int,
        search: String
    ): Observable<Response<SearchAnimeQuery.Data>> {
        val query = SearchAnimeQuery.builder().page(page).search(search).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchManga(
        page: Int,
        search: String
    ): Observable<Response<SearchMangaQuery.Data>> {
        val query = SearchMangaQuery.builder().page(page).search(search).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchCharacters(
        page: Int,
        search: String
    ): Observable<Response<SearchCharactersQuery.Data>> {
        val query = SearchCharactersQuery.builder().page(page).search(search).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchStaffs(
        page: Int,
        search: String
    ): Observable<Response<SearchStaffsQuery.Data>> {
        val query = SearchStaffsQuery.builder().page(page).search(search).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchStudios(
        page: Int,
        search: String
    ): Observable<Response<SearchStudiosQuery.Data>> {
        val query = SearchStudiosQuery.builder().page(page).search(search).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}