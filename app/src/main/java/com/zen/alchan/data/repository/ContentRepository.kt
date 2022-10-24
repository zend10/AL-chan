package com.zen.alchan.data.repository

import com.apollographql.apollo.api.Response
import com.zen.alchan.data.response.HomeData
import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.alchan.helper.enums.Source
import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Page
import io.reactivex.Observable
import type.MediaType

interface ContentRepository {
    fun getHomeData(source: Source? = null): Observable<HomeData>
    fun getGenres(): Observable<List<Genre>>
    fun getTags(): Observable<List<MediaTag>>
    fun searchMedia(searchQuery: String, type: MediaType, page: Int): Observable<Page<Media>>
}