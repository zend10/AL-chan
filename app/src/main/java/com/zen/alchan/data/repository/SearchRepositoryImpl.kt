package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.SearchDataSource
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.libs.SingleLiveEvent
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

    @SuppressLint("CheckResult")
    override fun searchAnime(
        page: Int,
        search: String,
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
    ) {
        _searchAnimeResponse.postValue(Resource.Loading())

        searchDataSource.searchAnime(
            page, search, season, seasonYear, format, status, isAdult, onList, source, countryOfOrigin, genreIn, tagIn, sort
        ).subscribeWith(object : Observer<Response<SearchAnimeQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<SearchAnimeQuery.Data>) {
                if (t.hasErrors()) {
                    _searchAnimeResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _searchAnimeResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _searchAnimeResponse.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun searchManga(
        page: Int,
        search: String,
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
    ) {
        _searchMangaResponse.postValue(Resource.Loading())

        searchDataSource.searchManga(
            page, search, startDateGreater, endDateLesser, format, status, isAdult, onList, source, countryOfOrigin, genreIn, tagIn, sort
        ).subscribeWith(object : Observer<Response<SearchMangaQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<SearchMangaQuery.Data>) {
                if (t.hasErrors()) {
                    _searchMangaResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _searchMangaResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _searchMangaResponse.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun searchCharacters(page: Int, search: String?, sort: List<CharacterSort>?) {
        _searchCharactersResponse.postValue(Resource.Loading())

        searchDataSource.searchCharacters(page, search, sort).subscribeWith(object : Observer<Response<SearchCharactersQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<SearchCharactersQuery.Data>) {
                if (t.hasErrors()) {
                    _searchCharactersResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _searchCharactersResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _searchCharactersResponse.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun searchStaffs(page: Int, search: String?, sort: List<StaffSort>?) {
        _searchStaffsResponse.postValue(Resource.Loading())

        searchDataSource.searchStaffs(page, search, sort).subscribeWith(object : Observer<Response<SearchStaffsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<SearchStaffsQuery.Data>) {
                if (t.hasErrors()) {
                    _searchStaffsResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _searchStaffsResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _searchStaffsResponse.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() { }
        })
    }

    @SuppressLint("CheckResult")
    override fun searchStudios(page: Int, search: String?, sort: List<StudioSort>?) {
        _searchStudiosResponse.postValue(Resource.Loading())

        searchDataSource.searchStudios(page, search, sort).subscribeWith(object : Observer<Response<SearchStudiosQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<SearchStudiosQuery.Data>) {
                if (t.hasErrors()) {
                    _searchStudiosResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _searchStudiosResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _searchStudiosResponse.postValue(Resource.Error(e.localizedMessage))
                e.printStackTrace()
            }

            override fun onComplete() { }
        })
    }
}