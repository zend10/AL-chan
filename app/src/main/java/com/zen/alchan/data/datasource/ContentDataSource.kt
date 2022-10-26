package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import type.MediaType

interface ContentDataSource {
    fun getHomeQuery(): Observable<Response<HomeDataQuery.Data>>
    fun getGenres(): Observable<Response<GenreQuery.Data>>
    fun getTags(): Observable<Response<TagQuery.Data>>
    fun searchMedia(searchQuery: String, type: MediaType, page: Int): Observable<Response<SearchMediaQuery.Data>>
    fun searchCharacter(searchQuery: String, page: Int): Observable<Response<SearchCharacterQuery.Data>>
    fun searchStaff(searchQuery: String, page: Int): Observable<Response<SearchStaffQuery.Data>>
    fun searchStudio(searchQuery: String, page: Int): Observable<Response<SearchStudioQuery.Data>>
    fun searchUser(searchQuery: String, page: Int): Observable<Response<SearchUserQuery.Data>>
}