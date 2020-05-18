package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import type.UserStatisticsSort

interface UserStatisticDataSource {
    fun getFormatStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsFormatsQuery.Data>>
    fun getStatusStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsStatusesQuery.Data>>
    fun getScoreStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsScoresQuery.Data>>
    fun getLengthStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsLengthsQuery.Data>>
    fun getReleaseYearStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsReleaseYearsQuery.Data>>
    fun getStartYearStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsStartYearsQuery.Data>>
    fun getGenreStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsGenresQuery.Data>>
    fun getTagStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsTagsQuery.Data>>
    fun getCountryStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsCountriesQuery.Data>>
    fun getVoiceActorStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsVoiceActorsQuery.Data>>
    fun getStaffStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsStaffsQuery.Data>>
    fun getStudioStatistic(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsStudiosQuery.Data>>
}