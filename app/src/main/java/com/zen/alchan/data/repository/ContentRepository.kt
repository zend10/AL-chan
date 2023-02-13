package com.zen.alchan.data.repository

import com.apollographql.apollo.api.Response
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.response.HomeData
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.*
import io.reactivex.Observable
import type.MediaType

interface ContentRepository {
    fun getHomeData(source: Source? = null): Observable<HomeData>
    fun getGenres(): Observable<List<Genre>>
    fun getTags(): Observable<List<MediaTag>>
    fun searchMedia(searchQuery: String, type: MediaType, mediaFilter: MediaFilter?, page: Int): Observable<Page<Media>>
    fun searchCharacter(searchQuery: String, page: Int): Observable<Page<Character>>
    fun searchStaff(searchQuery: String, page: Int): Observable<Page<Staff>>
    fun searchStudio(searchQuery: String, page: Int): Observable<Page<Studio>>
    fun searchUser(searchQuery: String, page: Int): Observable<Page<User>>
}