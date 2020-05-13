package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.SeasonalAnime
import com.zen.alchan.helper.enums.SeasonalCategory
import type.*

interface SearchRepository {
    val searchAnimeResponse: LiveData<Resource<SearchAnimeQuery.Data>>
    val searchMangaResponse: LiveData<Resource<SearchMangaQuery.Data>>
    val searchCharactersResponse: LiveData<Resource<SearchCharactersQuery.Data>>
    val searchStaffsResponse: LiveData<Resource<SearchStaffsQuery.Data>>
    val searchStudiosResponse: LiveData<Resource<SearchStudiosQuery.Data>>

    val seasonalAnimeTvResponse: LiveData<Resource<SeasonalAnimeQuery.Data>>
    val seasonalAnimeTvData: LiveData<List<SeasonalAnime>>
    val seasonalAnimeTvShortResponse: LiveData<Resource<SeasonalAnimeQuery.Data>>
    val seasonalAnimeTvShortData: LiveData<List<SeasonalAnime>>
    val seasonalAnimeMovieResponse: LiveData<Resource<SeasonalAnimeQuery.Data>>
    val seasonalAnimeMovieData: LiveData<List<SeasonalAnime>>
    val seasonalAnimeOthersResponse: LiveData<Resource<SeasonalAnimeQuery.Data>>
    val seasonalAnimeOthersData: LiveData<List<SeasonalAnime>>

    fun searchAnime(
        page: Int,
        search: String,
        season: MediaSeason? = null,
        seasonYear: Int? = null,
        format: MediaFormat? = null,
        status: MediaStatus? = null,
        isAdult: Boolean? = null,
        onList: Boolean? = null,
        source: MediaSource? = null,
        countryOfOrigin: String? = null,
        genreIn: List<String?>? = null,
        tagIn: List<String?>? = null,
        sort: List<MediaSort>? = null
    )

    fun searchManga(
        page: Int,
        search: String,
        startDateGreater: Int? = null,
        endDateLesser: Int? = null,
        format: MediaFormat? = null,
        status: MediaStatus? = null,
        isAdult: Boolean? = null,
        onList: Boolean? = null,
        source: MediaSource? = null,
        countryOfOrigin: String? = null,
        genreIn: List<String?>? = null,
        tagIn: List<String?>? = null,
        sort: List<MediaSort>? = null
    )

    fun searchCharacters(page: Int, search: String?, sort: List<CharacterSort>? = null)
    fun searchStaffs(page: Int, search: String?, sort: List<StaffSort>? = null)
    fun searchStudios(page: Int, search: String?, sort: List<StudioSort>? = null)

    fun getSeasonalAnime(
        page: Int,
        season: MediaSeason?,
        seasonYear: Int?,
        status: MediaStatus?,
        seasonalCategory: SeasonalCategory,
        isAdult: Boolean,
        onList: Boolean?,
        sort: List<MediaSort>
    )
}