package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.zen.alchan.helper.enums.CountryCode
import io.reactivex.Observable
import type.*

interface SearchDataSource {
    fun searchAnime(
        page: Int,
        search: String?,
        season: MediaSeason?,
        seasonYear: Int?,
        format: MediaFormat?,
        status: MediaStatus?,
        isAdult: Boolean?,
        onList: Boolean?,
        source: MediaSource?,
        countryOfOrigin: String?,
        genreIn: List<String?>?,
        tagIn: List<String?>?,
        sort: List<MediaSort>?
    ): Observable<Response<SearchAnimeQuery.Data>>

    fun searchManga(
        page: Int,
        search: String?,
        startDateGreater: Int?,
        endDateLesser: Int?,
        format: MediaFormat?,
        status: MediaStatus?,
        isAdult: Boolean?,
        onList: Boolean?,
        source: MediaSource?,
        countryOfOrigin: String?,
        genreIn: List<String?>?,
        tagIn: List<String?>?,
        sort: List<MediaSort>?
    ): Observable<Response<SearchMangaQuery.Data>>

    fun searchCharacters(page: Int, search: String?, sort: List<CharacterSort>?): Observable<Response<SearchCharactersQuery.Data>>
    fun searchStaffs(page: Int, search: String?, sort: List<StaffSort>?): Observable<Response<SearchStaffsQuery.Data>>
    fun searchStudios(page: Int, search: String?, sort: List<StudioSort>?): Observable<Response<SearchStudiosQuery.Data>>

    fun getSeasonalAnime(
        page: Int,
        season: MediaSeason?,
        seasonYear: Int?,
        status: MediaStatus?,
        formatIn: List<MediaFormat>,
        isAdult: Boolean,
        onList: Boolean?,
        sort: List<MediaSort>
    ): Observable<Response<SeasonalAnimeQuery.Data>>

    fun searchMediaImages(
        page: Int,
        idIn: List<Int>
    ): Observable<Response<MediaImageQuery.Data>>
}