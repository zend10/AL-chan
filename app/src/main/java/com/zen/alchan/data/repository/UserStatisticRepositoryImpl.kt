package com.zen.alchan.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.datasource.SearchDataSource
import com.zen.alchan.data.datasource.UserStatisticDataSource
import com.zen.alchan.data.localstorage.UserManager
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.libs.SingleLiveEvent
import com.zen.alchan.helper.utils.AndroidUtility
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import type.UserStatisticsSort

class UserStatisticRepositoryImpl(private val userStatisticDataSource: UserStatisticDataSource,
                                  private val searchDataSource: SearchDataSource,
                                  private val userManager: UserManager
) : UserStatisticRepository {

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

    val userId: Int?
        get() = userManager.viewerData?.id

    @SuppressLint("CheckResult")
    override fun getFormatStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _formatStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getFormatStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_formatStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getStatusStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _statusStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getStatusStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_statusStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getScoreStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _scoreStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getScoreStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_scoreStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getLengthStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _lengthStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getLengthStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_lengthStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getReleaseYearStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _releaseYearStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getReleaseYearStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_releaseYearStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getStartYearStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _startYearStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getStartYearStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_startYearStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getGenreStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _genreStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getGenreStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_genreStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getTagStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _tagStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getTagStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_tagStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getCountryStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _countryStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getCountryStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_countryStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getVoiceActorStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _voiceActorStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getVoiceActorStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_voiceActorStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getStaffStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _staffStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getStaffStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_staffStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun getStudioStatistic(sort: List<UserStatisticsSort>) {
        if (userId == null) {
            return
        }

        _studioStatisticResponse.postValue(Resource.Loading())
        userStatisticDataSource.getStudioStatistic(userId!!, sort).subscribeWith(AndroidUtility.rxApolloCallback(_studioStatisticResponse))
    }

    @SuppressLint("CheckResult")
    override fun searchMediaImage(page: Int, idIn: List<Int>) {
        searchDataSource.searchMediaImages(page, idIn).subscribeWith(AndroidUtility.rxApolloCallback(_searchMediaImageResponse))
    }
}