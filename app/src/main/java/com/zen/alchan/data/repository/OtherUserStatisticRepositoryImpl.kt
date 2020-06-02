package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.SearchDataSource
import com.zen.alchan.data.datasource.UserStatisticDataSource
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.libs.SingleLiveEvent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.UserStatisticsSort

class OtherUserStatisticRepositoryImpl(private val userStatisticDataSource: UserStatisticDataSource,
                                       private val searchDataSource: SearchDataSource
) : OtherUserStatisticRepository {

    private val _formatStatisticResponse = SingleLiveEvent<Resource<UserStatisticsFormatsQuery.Data>>()
    override val formatStatisticResponse: LiveData<Resource<UserStatisticsFormatsQuery.Data>>
        get() = _formatStatisticResponse

    private val _statusStatisticResponse = SingleLiveEvent<Resource<UserStatisticsStatusesQuery.Data>>()
    override val statusStatisticResponse: LiveData<Resource<UserStatisticsStatusesQuery.Data>>
        get() = _statusStatisticResponse

    private val _scoreStatisticResponse = SingleLiveEvent<Resource<UserStatisticsScoresQuery.Data>>()
    override val scoreStatisticResponse: LiveData<Resource<UserStatisticsScoresQuery.Data>>
        get() = _scoreStatisticResponse

    private val _lengthStatisticResponse = SingleLiveEvent<Resource<UserStatisticsLengthsQuery.Data>>()
    override val lengthStatisticResponse: LiveData<Resource<UserStatisticsLengthsQuery.Data>>
        get() = _lengthStatisticResponse

    private val _releaseYearStatisticResponse = SingleLiveEvent<Resource<UserStatisticsReleaseYearsQuery.Data>>()
    override val releaseYearStatisticResponse: LiveData<Resource<UserStatisticsReleaseYearsQuery.Data>>
        get() = _releaseYearStatisticResponse

    private val _startYearStatisticResponse = SingleLiveEvent<Resource<UserStatisticsStartYearsQuery.Data>>()
    override val startYearStatisticResponse: LiveData<Resource<UserStatisticsStartYearsQuery.Data>>
        get() = _startYearStatisticResponse

    private val _genreStatisticResponse = SingleLiveEvent<Resource<UserStatisticsGenresQuery.Data>>()
    override val genreStatisticResponse: LiveData<Resource<UserStatisticsGenresQuery.Data>>
        get() = _genreStatisticResponse

    private val _tagStatisticResponse = SingleLiveEvent<Resource<UserStatisticsTagsQuery.Data>>()
    override val tagStatisticResponse: LiveData<Resource<UserStatisticsTagsQuery.Data>>
        get() = _tagStatisticResponse

    private val _countryStatisticResponse = SingleLiveEvent<Resource<UserStatisticsCountriesQuery.Data>>()
    override val countryStatisticResponse: LiveData<Resource<UserStatisticsCountriesQuery.Data>>
        get() = _countryStatisticResponse

    private val _voiceActorStatisticResponse = SingleLiveEvent<Resource<UserStatisticsVoiceActorsQuery.Data>>()
    override val voiceActorStatisticResponse: LiveData<Resource<UserStatisticsVoiceActorsQuery.Data>>
        get() = _voiceActorStatisticResponse

    private val _staffStatisticResponse = SingleLiveEvent<Resource<UserStatisticsStaffsQuery.Data>>()
    override val staffStatisticResponse: LiveData<Resource<UserStatisticsStaffsQuery.Data>>
        get() = _staffStatisticResponse

    private val _studioStatisticResponse = SingleLiveEvent<Resource<UserStatisticsStudiosQuery.Data>>()
    override val studioStatisticResponse: LiveData<Resource<UserStatisticsStudiosQuery.Data>>
        get() = _studioStatisticResponse

    private val _searchMediaImageResponse = SingleLiveEvent<Resource<MediaImageQuery.Data>>()
    override val searchMediaImageResponse: LiveData<Resource<MediaImageQuery.Data>>
        get() = _searchMediaImageResponse

    @SuppressLint("CheckResult")
    override fun getFormatStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _formatStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getFormatStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsFormatsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsFormatsQuery.Data>) {
                if (t.hasErrors()) {
                    _formatStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _formatStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _formatStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getStatusStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _statusStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getStatusStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsStatusesQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsStatusesQuery.Data>) {
                if (t.hasErrors()) {
                    _statusStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _statusStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _statusStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getScoreStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _scoreStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getScoreStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsScoresQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsScoresQuery.Data>) {
                if (t.hasErrors()) {
                    _scoreStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _scoreStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _scoreStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getLengthStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _lengthStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getLengthStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsLengthsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsLengthsQuery.Data>) {
                if (t.hasErrors()) {
                    _lengthStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _lengthStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _lengthStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getReleaseYearStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _releaseYearStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getReleaseYearStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsReleaseYearsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsReleaseYearsQuery.Data>) {
                if (t.hasErrors()) {
                    _releaseYearStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _releaseYearStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _releaseYearStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getStartYearStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _startYearStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getStartYearStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsStartYearsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsStartYearsQuery.Data>) {
                if (t.hasErrors()) {
                    _startYearStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _startYearStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _startYearStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getGenreStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _genreStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getGenreStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsGenresQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsGenresQuery.Data>) {
                if (t.hasErrors()) {
                    _genreStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _genreStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _genreStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getTagStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _tagStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getTagStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsTagsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsTagsQuery.Data>) {
                if (t.hasErrors()) {
                    _tagStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _tagStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _tagStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getCountryStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _countryStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getCountryStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsCountriesQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsCountriesQuery.Data>) {
                if (t.hasErrors()) {
                    _countryStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _countryStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _countryStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getVoiceActorStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _voiceActorStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getVoiceActorStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsVoiceActorsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsVoiceActorsQuery.Data>) {
                if (t.hasErrors()) {
                    _voiceActorStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _voiceActorStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _voiceActorStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getStaffStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _staffStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getStaffStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsStaffsQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsStaffsQuery.Data>) {
                if (t.hasErrors()) {
                    _staffStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _staffStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _staffStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun getStudioStatistic(userId: Int, sort: List<UserStatisticsSort>) {
        _studioStatisticResponse.postValue(Resource.Loading())

        userStatisticDataSource.getStudioStatistic(userId, sort).subscribeWith(object : Observer<Response<UserStatisticsStudiosQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<UserStatisticsStudiosQuery.Data>) {
                if (t.hasErrors()) {
                    _studioStatisticResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _studioStatisticResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _studioStatisticResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() {}
        })
    }

    @SuppressLint("CheckResult")
    override fun searchMediaImage(page: Int, idIn: List<Int>) {
        searchDataSource.searchMediaImages(page, idIn).subscribeWith(object : Observer<Response<MediaImageQuery.Data>> {
            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: Response<MediaImageQuery.Data>) {
                if (t.hasErrors()) {
                    _searchMediaImageResponse.postValue(Resource.Error(t.errors!![0].message))
                } else {
                    _searchMediaImageResponse.postValue(Resource.Success(t.data!!))
                }
            }

            override fun onError(e: Throwable) {
                _searchMediaImageResponse.postValue(Resource.Error(e.localizedMessage))
            }

            override fun onComplete() { }
        })
    }
}