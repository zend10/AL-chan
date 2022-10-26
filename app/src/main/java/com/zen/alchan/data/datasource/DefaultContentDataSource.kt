package com.zen.alchan.data.datasource

import GenreQuery
import HomeDataQuery
import SearchCharacterQuery
import SearchMediaQuery
import SearchStaffQuery
import SearchStudioQuery
import SearchUserQuery
import TagQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable
import type.MediaType

class DefaultContentDataSource(private val apolloHandler: ApolloHandler, private val statusVersion: Int) : ContentDataSource {

    override fun getHomeQuery(): Observable<Response<HomeDataQuery.Data>> {
        val query = HomeDataQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getGenres(): Observable<Response<GenreQuery.Data>> {
        val query = GenreQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getTags(): Observable<Response<TagQuery.Data>> {
        val query = TagQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun searchMedia(
        searchQuery: String,
        type: MediaType,
        page: Int
    ): Observable<Response<SearchMediaQuery.Data>> {
        val query = SearchMediaQuery(
            search = Input.fromNullable(searchQuery),
            type = Input.fromNullable(type),
            page = Input.fromNullable(page),
            statusVersion = Input.fromNullable(statusVersion)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun searchCharacter(
        searchQuery: String,
        page: Int
    ): Observable<Response<SearchCharacterQuery.Data>> {
        val query = SearchCharacterQuery(
            search = Input.fromNullable(searchQuery),
            page = Input.fromNullable(page)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun searchStaff(
        searchQuery: String,
        page: Int
    ): Observable<Response<SearchStaffQuery.Data>> {
        val query = SearchStaffQuery(
            search = Input.fromNullable(searchQuery),
            page = Input.fromNullable(page)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun searchStudio(
        searchQuery: String,
        page: Int
    ): Observable<Response<SearchStudioQuery.Data>> {
        val query = SearchStudioQuery(
            search = Input.fromNullable(searchQuery),
            page = Input.fromNullable(page)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun searchUser(
        searchQuery: String,
        page: Int
    ): Observable<Response<SearchUserQuery.Data>> {
        val query = SearchUserQuery(
            search = Input.fromNullable(searchQuery),
            page = Input.fromNullable(page)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }
}