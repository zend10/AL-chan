package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.SearchDataSource
import com.zen.alchan.data.network.Converter
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.SeasonalAnime
import com.zen.alchan.helper.enums.SeasonalCategory
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.pojo.MediaFilterData
import com.zen.alchan.helper.utils.AndroidUtility
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.*

class SearchRepositoryImpl(private val searchDataSource: SearchDataSource) : SearchRepository {

    private val _searchAnimeResponse = SingleLiveEvent<Resource<SearchAnimeQuery.Data>>()
    override val searchAnimeResponse: LiveData<Resource<SearchAnimeQuery.Data>>
        get() = _searchAnimeResponse

    private val _searchMangaResponse = SingleLiveEvent<Resource<SearchMangaQuery.Data>>()
    override val searchMangaResponse: LiveData<Resource<SearchMangaQuery.Data>>
        get() = _searchMangaResponse

    private val _searchCharactersResponse = SingleLiveEvent<Resource<SearchCharactersQuery.Data>>()
    override val searchCharactersResponse: LiveData<Resource<SearchCharactersQuery.Data>>
        get() = _searchCharactersResponse

    private val _searchStaffsResponse = SingleLiveEvent<Resource<SearchStaffsQuery.Data>>()
    override val searchStaffsResponse: LiveData<Resource<SearchStaffsQuery.Data>>
        get() = _searchStaffsResponse

    private val _searchStudiosResponse = SingleLiveEvent<Resource<SearchStudiosQuery.Data>>()
    override val searchStudiosResponse: LiveData<Resource<SearchStudiosQuery.Data>>
        get() = _searchStudiosResponse

    private val _searchUsersResponse = SingleLiveEvent<Resource<SearchUsersQuery.Data>>()
    override val searchUsersResponse: LiveData<Resource<SearchUsersQuery.Data>>
        get() = _searchUsersResponse

    private val _seasonalAnimeTvResponse = SingleLiveEvent<Resource<SeasonalAnimeQuery.Data>>()
    override val seasonalAnimeTvResponse: LiveData<Resource<SeasonalAnimeQuery.Data>>
        get() = _seasonalAnimeTvResponse

    private val _seasonalAnimeTvData = SingleLiveEvent<List<SeasonalAnime>>()
    override val seasonalAnimeTvData: LiveData<List<SeasonalAnime>>
        get() = _seasonalAnimeTvData

    private val _seasonalAnimeTvShortResponse = SingleLiveEvent<Resource<SeasonalAnimeQuery.Data>>()
    override val seasonalAnimeTvShortResponse: LiveData<Resource<SeasonalAnimeQuery.Data>>
        get() = _seasonalAnimeTvShortResponse

    private val _seasonalAnimeTvShortData = SingleLiveEvent<List<SeasonalAnime>>()
    override val seasonalAnimeTvShortData: LiveData<List<SeasonalAnime>>
        get() = _seasonalAnimeTvShortData

    private val _seasonalAnimeMovieResponse = SingleLiveEvent<Resource<SeasonalAnimeQuery.Data>>()
    override val seasonalAnimeMovieResponse: LiveData<Resource<SeasonalAnimeQuery.Data>>
        get() = _seasonalAnimeMovieResponse

    private val _seasonalAnimeMovieData = SingleLiveEvent<List<SeasonalAnime>>()
    override val seasonalAnimeMovieData: LiveData<List<SeasonalAnime>>
        get() = _seasonalAnimeMovieData

    private val _seasonalAnimeOthersResponse = SingleLiveEvent<Resource<SeasonalAnimeQuery.Data>>()
    override val seasonalAnimeOthersResponse: LiveData<Resource<SeasonalAnimeQuery.Data>>
        get() = _seasonalAnimeOthersResponse

    private val _seasonalAnimeOthersData = SingleLiveEvent<List<SeasonalAnime>>()
    override val seasonalAnimeOthersData: LiveData<List<SeasonalAnime>>
        get() = _seasonalAnimeOthersData

    private val _airingScheduleResponse = SingleLiveEvent<Resource<AiringScheduleQuery.Data>>()
    override val airingScheduleResponse: LiveData<Resource<AiringScheduleQuery.Data>>
        get() = _airingScheduleResponse

    @SuppressLint("CheckResult")
    override fun searchAnime(
        page: Int,
        search: String,
        filterData: MediaFilterData?
    ) {
        _searchAnimeResponse.postValue(Resource.Loading())

        searchDataSource.searchAnime(
            page,
            search,
            if (filterData?.selectedMediaSort != null) listOf(filterData.selectedMediaSort!!) else null,
            filterData?.selectedFormats,
            filterData?.selectedStatuses,
            filterData?.selectedSources,
            filterData?.selectedCountry?.name,
            filterData?.selectedSeason,
            filterData?.selectedYear?.minValue,
            filterData?.selectedYear?.maxValue,
            filterData?.selectedIsAdult,
            filterData?.selectedOnList,
            filterData?.selectedGenres,
            filterData?.selectedExcludedGenres,
            filterData?.selectedMinimumTagRank,
            filterData?.selectedTagNames,
            filterData?.selectedExcludedTagNames,
            filterData?.selectedLicensed,
            filterData?.selectedEpisodes?.minValue,
            filterData?.selectedEpisodes?.maxValue,
            filterData?.selectedDuration?.minValue,
            filterData?.selectedDuration?.maxValue,
            filterData?.selectedAverageScore?.minValue,
            filterData?.selectedAverageScore?.maxValue,
            filterData?.selectedPopularity?.minValue,
            filterData?.selectedPopularity?.maxValue

        ).subscribeWith(AndroidUtility.rxApolloCallback(_searchAnimeResponse))
    }

    @SuppressLint("CheckResult")
    override fun searchManga(
        page: Int,
        search: String,
        filterData: MediaFilterData?
    ) {
        _searchMangaResponse.postValue(Resource.Loading())

        searchDataSource.searchManga(
            page,
            search,
            if (filterData?.selectedMediaSort != null) listOf(filterData.selectedMediaSort!!) else null,
            filterData?.selectedFormats,
            filterData?.selectedStatuses,
            filterData?.selectedSources,
            filterData?.selectedCountry?.name,
            filterData?.selectedSeason,
            filterData?.selectedYear?.minValue,
            filterData?.selectedYear?.maxValue,
            filterData?.selectedIsAdult,
            filterData?.selectedOnList,
            filterData?.selectedGenres,
            filterData?.selectedExcludedGenres,
            filterData?.selectedMinimumTagRank,
            filterData?.selectedTagNames,
            filterData?.selectedExcludedTagNames,
            filterData?.selectedLicensed,
            filterData?.selectedChapters?.minValue,
            filterData?.selectedChapters?.maxValue,
            filterData?.selectedVolumes?.minValue,
            filterData?.selectedVolumes?.maxValue,
            filterData?.selectedAverageScore?.minValue,
            filterData?.selectedAverageScore?.maxValue,
            filterData?.selectedPopularity?.minValue,
            filterData?.selectedPopularity?.maxValue
        ).subscribeWith(AndroidUtility.rxApolloCallback(_searchMangaResponse))
    }

    @SuppressLint("CheckResult")
    override fun searchCharacters(page: Int, search: String?, sort: List<CharacterSort>?) {
        _searchCharactersResponse.postValue(Resource.Loading())
        searchDataSource.searchCharacters(page, search, sort).subscribeWith(AndroidUtility.rxApolloCallback(_searchCharactersResponse))
    }

    @SuppressLint("CheckResult")
    override fun searchStaffs(page: Int, search: String?, sort: List<StaffSort>?) {
        _searchStaffsResponse.postValue(Resource.Loading())
        searchDataSource.searchStaffs(page, search, sort).subscribeWith(AndroidUtility.rxApolloCallback(_searchStaffsResponse))
    }

    @SuppressLint("CheckResult")
    override fun searchStudios(page: Int, search: String?, sort: List<StudioSort>?) {
        _searchStudiosResponse.postValue(Resource.Loading())
        searchDataSource.searchStudios(page, search, sort).subscribeWith(AndroidUtility.rxApolloCallback(_searchStudiosResponse))
    }

    @SuppressLint("CheckResult")
    override fun searchUsers(page: Int, search: String?, sort: List<UserSort>?) {
        _searchUsersResponse.postValue(Resource.Loading())
        searchDataSource.searchUsers(page, search, sort).subscribeWith(AndroidUtility.rxApolloCallback(_searchUsersResponse))
    }

    @SuppressLint("CheckResult")
    override fun getSeasonalAnime(
        page: Int,
        season: MediaSeason?,
        seasonYear: Int?,
        status: MediaStatus?,
        seasonalCategory: SeasonalCategory,
        isAdult: Boolean,
        onList: Boolean?,
        sort: List<MediaSort>
    ) {
        getSeasonalResponseLiveData(seasonalCategory).postValue(Resource.Loading())

        val formatIn = when (seasonalCategory) {
            SeasonalCategory.TV -> listOf(MediaFormat.TV)
            SeasonalCategory.TV_SHORT -> listOf(MediaFormat.TV_SHORT)
            SeasonalCategory.MOVIE -> listOf(MediaFormat.MOVIE)
            SeasonalCategory.OTHERS -> listOf(MediaFormat.OVA, MediaFormat.ONA, MediaFormat.SPECIAL)
        }

        searchDataSource.getSeasonalAnime(
            page, season, seasonYear, status, formatIn, isAdult, onList, sort
        ).subscribeWith(object : Observer<Response<SeasonalAnimeQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<SeasonalAnimeQuery.Data>) {
                if (t.hasErrors()) {
                    getSeasonalResponseLiveData(seasonalCategory).postValue(Resource.Error(t.errors!![0].message))
                } else {
                    val seasonalAnime = Converter.convertSeasonalAnime(t.data?.page?.media)
                    getSeasonalDataLiveData(seasonalCategory).postValue(seasonalAnime)
                    getSeasonalResponseLiveData(seasonalCategory).postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                getSeasonalResponseLiveData(seasonalCategory).postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() { }
        })
    }

    private fun getSeasonalResponseLiveData(seasonalCategory: SeasonalCategory): SingleLiveEvent<Resource<SeasonalAnimeQuery.Data>> {
        return when (seasonalCategory) {
            SeasonalCategory.TV -> _seasonalAnimeTvResponse
            SeasonalCategory.TV_SHORT -> _seasonalAnimeTvShortResponse
            SeasonalCategory.MOVIE -> _seasonalAnimeMovieResponse
            SeasonalCategory.OTHERS -> _seasonalAnimeOthersResponse
        }
    }

    private fun getSeasonalDataLiveData(seasonalCategory: SeasonalCategory): SingleLiveEvent<List<SeasonalAnime>> {
        return when (seasonalCategory) {
            SeasonalCategory.TV -> _seasonalAnimeTvData
            SeasonalCategory.TV_SHORT -> _seasonalAnimeTvShortData
            SeasonalCategory.MOVIE -> _seasonalAnimeMovieData
            SeasonalCategory.OTHERS -> _seasonalAnimeOthersData
        }
    }

    @SuppressLint("CheckResult")
    override fun getAiringSchedule(page: Int, airingAtGreater: Int, airingAtLesser: Int) {
        _airingScheduleResponse.postValue(Resource.Loading())
        searchDataSource.getAiringSchedule(page, airingAtGreater, airingAtLesser).subscribeWith(AndroidUtility.rxApolloCallback(_airingScheduleResponse))
    }
}