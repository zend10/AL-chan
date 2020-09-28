package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.zen.alchan.helper.enums.CountryCode
import io.reactivex.Observable
import type.*

interface SearchDataSource {
    fun searchAnime(
        page: Int,
        search: String?,
        sort: List<MediaSort>?,
        formatIn: List<MediaFormat>?,
        statusIn: List<MediaStatus>?,
        sourceIn: List<MediaSource>?,
        countryOfOrigin: String?,
        season: MediaSeason?,
        startDateGreater: Int?,
        startDateLesser: Int?,
        isAdult: Boolean?,
        onList: Boolean?,
        genreIn: List<String>?,
        genreNotIn: List<String>?,
        minimumTagRank: Int?,
        tagIn: List<String>?,
        tagNotIn: List<String>?,
        licensedByIn: List<String>?,
        episodesGreater: Int?,
        episodesLesser: Int?,
        durationGreater: Int?,
        durationLesser: Int?,
        averageScoreGreater: Int?,
        averageScoreLesser: Int?,
        popularityGreater: Int?,
        popularityLesser: Int?
    ): Observable<Response<SearchAnimeQuery.Data>>

    fun searchManga(
        page: Int,
        search: String?,
        sort: List<MediaSort>?,
        formatIn: List<MediaFormat>?,
        statusIn: List<MediaStatus>?,
        sourceIn: List<MediaSource>?,
        countryOfOrigin: String?,
        season: MediaSeason?,
        startDateGreater: Int?,
        startDateLesser: Int?,
        isAdult: Boolean?,
        onList: Boolean?,
        genreIn: List<String>?,
        genreNotIn: List<String>?,
        minimumTagRank: Int?,
        tagIn: List<String>?,
        tagNotIn: List<String>?,
        licensedByIn: List<String>?,
        chaptersGreater: Int?,
        chaptersLesser: Int?,
        volumesGreater: Int?,
        volumesLesser: Int?,
        averageScoreGreater: Int?,
        averageScoreLesser: Int?,
        popularityGreater: Int?,
        popularityLesser: Int?
    ): Observable<Response<SearchMangaQuery.Data>>

    fun searchCharacters(page: Int, search: String?, sort: List<CharacterSort>?): Observable<Response<SearchCharactersQuery.Data>>
    fun searchStaffs(page: Int, search: String?, sort: List<StaffSort>?): Observable<Response<SearchStaffsQuery.Data>>
    fun searchStudios(page: Int, search: String?, sort: List<StudioSort>?): Observable<Response<SearchStudiosQuery.Data>>
    fun searchUsers(page: Int, search: String?, sort: List<UserSort>?): Observable<Response<SearchUsersQuery.Data>>

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

    fun searchCharacterImages(
        page: Int,
        idIn: List<Int>
    ): Observable<Response<CharacterImageQuery.Data>>
}