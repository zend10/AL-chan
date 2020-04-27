package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable

interface MediaDataSource {
    fun getGenre(): Observable<Response<GenreQuery.Data>>
    fun getMedia(id: Int): Observable<Response<MediaQuery.Data>>
    fun checkMediaStatus(userId: Int, mediaId: Int): Observable<Response<MediaStatusQuery.Data>>
    fun getMediaOverview(id: Int): Observable<Response<MediaOverviewQuery.Data>>
    fun getMediaCharacters(id: Int, page: Int): Observable<Response<MediaCharactersQuery.Data>>
    fun getMediaStaffs(id: Int, page: Int): Observable<Response<MediaStaffsQuery.Data>>
}