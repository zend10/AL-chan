package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import io.reactivex.Observer
import type.MediaSort
import type.MediaType

interface BrowseDataSource {
    fun getCharacter(id: Int): Observable<Response<CharacterQuery.Data>>
    fun getCharacterMedia(id: Int, page: Int): Observable<Response<CharacterMediaConnectionQuery.Data>>
    fun checkCharacterIsFavorite(id: Int): Observable<Response<CharacterIsFavoriteQuery.Data>>

    fun getStaff(id: Int): Observable<Response<StaffQuery.Data>>
    fun getStaffBio(id: Int): Observable<Response<StaffBioQuery.Data>>
    fun getStaffCharacter(id: Int, page: Int): Observable<Response<StaffCharacterConnectionQuery.Data>>
    fun getStaffMedia(id: Int, type: MediaType, page: Int): Observable<Response<StaffMediaConnectionQuery.Data>>
    fun checkStaffIsFavorite(id: Int): Observable<Response<StaffIsFavoriteQuery.Data>>

    fun getStudio(id: Int): Observable<Response<StudioQuery.Data>>
    fun getStudioMedia(id: Int, page: Int, sort: MediaSort): Observable<Response<StudioMediaConnectionQuery.Data>>
    fun checkStudioIsFavorite(id: Int): Observable<Response<StudioIsFavoriteQuery.Data>>

    fun getIdFromName(name: String): Observable<Response<IdFromNameQuery.Data>>
}