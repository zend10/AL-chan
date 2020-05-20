package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import type.UserStatisticsSort

interface UserStatisticRepository {
    val formatStatisticResponse: LiveData<Resource<UserStatisticsFormatsQuery.Data>>
    val statusStatisticResponse: LiveData<Resource<UserStatisticsStatusesQuery.Data>>
    val scoreStatisticResponse: LiveData<Resource<UserStatisticsScoresQuery.Data>>
    val lengthStatisticResponse: LiveData<Resource<UserStatisticsLengthsQuery.Data>>
    val releaseYearStatisticResponse: LiveData<Resource<UserStatisticsReleaseYearsQuery.Data>>
    val startYearStatisticResponse: LiveData<Resource<UserStatisticsStartYearsQuery.Data>>
    val genreStatisticResponse: LiveData<Resource<UserStatisticsGenresQuery.Data>>
    val tagStatisticResponse: LiveData<Resource<UserStatisticsTagsQuery.Data>>
    val countryStatisticResponse: LiveData<Resource<UserStatisticsCountriesQuery.Data>>
    val voiceActorStatisticResponse: LiveData<Resource<UserStatisticsVoiceActorsQuery.Data>>
    val staffStatisticResponse: LiveData<Resource<UserStatisticsStaffsQuery.Data>>
    val studioStatisticResponse: LiveData<Resource<UserStatisticsStudiosQuery.Data>>

    val searchMediaImageResponse: LiveData<Resource<MediaImageQuery.Data>>

    fun getFormatStatistic(sort: List<UserStatisticsSort>)
    fun getStatusStatistic(sort: List<UserStatisticsSort>)
    fun getScoreStatistic(sort: List<UserStatisticsSort>)
    fun getLengthStatistic(sort: List<UserStatisticsSort>)
    fun getReleaseYearStatistic(sort: List<UserStatisticsSort>)
    fun getStartYearStatistic(sort: List<UserStatisticsSort>)
    fun getGenreStatistic(sort: List<UserStatisticsSort>)
    fun getTagStatistic(sort: List<UserStatisticsSort>)
    fun getCountryStatistic(sort: List<UserStatisticsSort>)
    fun getVoiceActorStatistic(sort: List<UserStatisticsSort>)
    fun getStaffStatistic(sort: List<UserStatisticsSort>)
    fun getStudioStatistic(sort: List<UserStatisticsSort>)

    fun searchMediaImage(page: Int, idIn: List<Int>)
}