package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import type.UserStatisticsSort

interface OtherUserStatisticRepository {
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

    fun getFormatStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getStatusStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getScoreStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getLengthStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getReleaseYearStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getStartYearStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getGenreStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getTagStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getCountryStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getVoiceActorStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getStaffStatistic(userId: Int, sort: List<UserStatisticsSort>)
    fun getStudioStatistic(userId: Int, sort: List<UserStatisticsSort>)

    fun searchMediaImage(page: Int, idIn: List<Int>)
}