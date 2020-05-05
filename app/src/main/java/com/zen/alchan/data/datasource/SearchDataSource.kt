package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable

interface SearchDataSource {
    fun searchAnime(page: Int, search: String): Observable<Response<SearchAnimeQuery.Data>>
    fun searchManga(page: Int, search: String): Observable<Response<SearchMangaQuery.Data>>
    fun searchCharacters(page: Int, search: String): Observable<Response<SearchCharactersQuery.Data>>
    fun searchStaffs(page: Int, search: String): Observable<Response<SearchStaffsQuery.Data>>
    fun searchStudios(page: Int, search: String): Observable<Response<SearchStudiosQuery.Data>>
}