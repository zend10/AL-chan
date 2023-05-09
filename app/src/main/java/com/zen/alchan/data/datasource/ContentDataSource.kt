package com.zen.alchan.data.datasource

import com.apollographql.apollo3.api.ApolloResponse
import com.zen.alchan.*
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.helper.enums.Sort
import com.zen.alchan.type.MediaSeason
import com.zen.alchan.type.MediaType
import io.reactivex.rxjava3.core.Observable

interface ContentDataSource {
    fun getHomeQuery(): Observable<ApolloResponse<HomeDataQuery.Data>>
    fun getGenres(): Observable<ApolloResponse<GenreQuery.Data>>
    fun getTags(): Observable<ApolloResponse<TagQuery.Data>>
    fun searchMedia(searchQuery: String, type: MediaType, mediaFilter: MediaFilter?, page: Int): Observable<ApolloResponse<SearchMediaQuery.Data>>
    fun searchCharacter(searchQuery: String, page: Int): Observable<ApolloResponse<SearchCharacterQuery.Data>>
    fun searchStaff(searchQuery: String, page: Int): Observable<ApolloResponse<SearchStaffQuery.Data>>
    fun searchStudio(searchQuery: String, page: Int): Observable<ApolloResponse<SearchStudioQuery.Data>>
    fun searchUser(searchQuery: String, page: Int): Observable<ApolloResponse<SearchUserQuery.Data>>
    fun getSeasonal(
        page: Int,
        year: Int,
        season: MediaSeason,
        sort: Sort,
        orderByDescending: Boolean,
        onlyShowOnList: Boolean?,
        showAdult: Boolean
    ): Observable<ApolloResponse<SearchMediaQuery.Data>>

    fun getAiringSchedule(page: Int, airingAtGreater: Int, airingAtLesser: Int): Observable<ApolloResponse<AiringScheduleQuery.Data>>
}