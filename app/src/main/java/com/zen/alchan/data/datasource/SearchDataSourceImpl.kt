package com.zen.alchan.data.datasource

import SearchAnimeQuery
import SearchCharactersQuery
import SearchMangaQuery
import SearchStaffsQuery
import SearchStudiosQuery
import com.apollographql.apollo.api.Input
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
        val query = SearchAnimeQuery(
            page = Input.fromNullable(page),
            search = Input.fromNullable(search)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchManga(
        page: Int,
        search: String
    ): Observable<Response<SearchMangaQuery.Data>> {
        val query = SearchMangaQuery(
            page = Input.fromNullable(page),
            search = Input.fromNullable(search)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchCharacters(
        page: Int,
        search: String
    ): Observable<Response<SearchCharactersQuery.Data>> {
        val query = SearchCharactersQuery(
            page = Input.fromNullable(page),
            search = Input.fromNullable(search)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchStaffs(
        page: Int,
        search: String
    ): Observable<Response<SearchStaffsQuery.Data>> {
        val query = SearchStaffsQuery(
            page = Input.fromNullable(page),
            search = Input.fromNullable(search)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun searchStudios(
        page: Int,
        search: String
    ): Observable<Response<SearchStudiosQuery.Data>> {
        val query = SearchStudiosQuery(
            page = Input.fromNullable(page),
            search = Input.fromNullable(search)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}