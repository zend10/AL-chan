package com.zen.alchan.data.datasource

import AiringScheduleQuery
import SearchMediaQuery
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.helper.enums.Sort
import io.reactivex.Observable
import type.MediaSeason
import type.MediaSort
import type.MediaType

interface ContentDataSource {
    fun getHomeQuery(): Observable<Response<HomeDataQuery.Data>>
    fun getGenres(): Observable<Response<GenreQuery.Data>>
    fun getTags(): Observable<Response<TagQuery.Data>>
    fun searchMedia(searchQuery: String, type: MediaType, mediaFilter: MediaFilter?, page: Int): Observable<Response<SearchMediaQuery.Data>>
    fun searchCharacter(searchQuery: String, page: Int): Observable<Response<SearchCharacterQuery.Data>>
    fun searchStaff(searchQuery: String, page: Int): Observable<Response<SearchStaffQuery.Data>>
    fun searchStudio(searchQuery: String, page: Int): Observable<Response<SearchStudioQuery.Data>>
    fun searchUser(searchQuery: String, page: Int): Observable<Response<SearchUserQuery.Data>>
    fun getSeasonal(
        page: Int,
        year: Int,
        season: MediaSeason,
        sort: Sort,
        orderByDescending: Boolean,
        onlyShowOnList: Boolean?,
        showAdult: Boolean
    ): Observable<Response<SearchMediaQuery.Data>>

    fun getAiringSchedule(page: Int, airingAtGreater: Int, airingAtLesser: Int): Observable<Response<AiringScheduleQuery.Data>>
}