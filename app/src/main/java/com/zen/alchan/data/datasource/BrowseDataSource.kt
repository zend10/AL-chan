package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import type.*

interface BrowseDataSource {
    fun getUserQuery(id: Int, sort: List<UserStatisticsSort>): Observable<Response<UserQuery.Data>>
    fun getMediaQuery(id: Int): Observable<Response<MediaQuery.Data>>
    fun getMediaCharactersQuery(id: Int, page: Int, language: StaffLanguage): Observable<Response<MediaCharactersQuery.Data>>
    fun getMediaStaffQuery(id: Int, page: Int): Observable<Response<MediaStaffQuery.Data>>
    fun getCharacterQuery(id: Int, page: Int, sort: List<MediaSort>, type: MediaType?, onList: Boolean?): Observable<Response<CharacterQuery.Data>>
    fun getStaffQuery(
        id: Int,
        page: Int,
        staffMediaSort: List<MediaSort>,
        characterSort: List<CharacterSort>,
        characterMediaSort: List<MediaSort>,
        onList: Boolean?
    ): Observable<Response<StaffQuery.Data>>
    fun getStudioQuery(id: Int, page: Int, sort: List<MediaSort>, onList: Boolean?): Observable<Response<StudioQuery.Data>>
}