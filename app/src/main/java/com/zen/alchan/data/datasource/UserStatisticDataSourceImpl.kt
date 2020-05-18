package com.zen.alchan.data.datasource

import UserStatisticsCountriesQuery
import UserStatisticsFormatsQuery
import UserStatisticsGenresQuery
import UserStatisticsLengthsQuery
import UserStatisticsReleaseYearsQuery
import UserStatisticsScoresQuery
import UserStatisticsStaffsQuery
import UserStatisticsStartYearsQuery
import UserStatisticsStatusesQuery
import UserStatisticsStudiosQuery
import UserStatisticsTagsQuery
import UserStatisticsVoiceActorsQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import type.UserStatisticsSort

class UserStatisticDataSourceImpl(private val apolloHandler: ApolloHandler) : UserStatisticDataSource {

    override fun getFormatStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsFormatsQuery.Data>> {
        val query = UserStatisticsFormatsQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStatusStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsStatusesQuery.Data>> {
        val query = UserStatisticsStatusesQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getScoreStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsScoresQuery.Data>> {
        val query = UserStatisticsScoresQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getLengthStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsLengthsQuery.Data>> {
        val query = UserStatisticsLengthsQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getReleaseYearStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsReleaseYearsQuery.Data>> {
        val query = UserStatisticsReleaseYearsQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStartYearStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsStartYearsQuery.Data>> {
        val query = UserStatisticsStartYearsQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getGenreStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsGenresQuery.Data>> {
        val query = UserStatisticsGenresQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getTagStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsTagsQuery.Data>> {
        val query = UserStatisticsTagsQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getCountryStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsCountriesQuery.Data>> {
        val query = UserStatisticsCountriesQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getVoiceActorStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsVoiceActorsQuery.Data>> {
        val query = UserStatisticsVoiceActorsQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStaffStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsStaffsQuery.Data>> {
        val query = UserStatisticsStaffsQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStudioStatistic(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserStatisticsStudiosQuery.Data>> {
        val query = UserStatisticsStudiosQuery(id = Input.fromNullable(userId), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}