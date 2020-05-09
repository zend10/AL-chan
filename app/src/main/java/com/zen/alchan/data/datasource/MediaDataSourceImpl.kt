package com.zen.alchan.data.datasource

import GenreQuery
import MediaCharactersQuery
import MediaOverviewQuery
import MediaQuery
import MediaStaffsQuery
import MediaStatusQuery
import ReleasingTodayQuery
import TrendingMediaQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import type.MediaSeason
import type.MediaType

class MediaDataSourceImpl(private val apolloHandler: ApolloHandler) : MediaDataSource {

    override fun getGenre(): Observable<Response<GenreQuery.Data>> {
        val query = GenreQuery()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMedia(id: Int): Observable<Response<MediaQuery.Data>> {
        val query = MediaQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkMediaStatus(
        userId: Int,
        mediaId: Int
    ): Observable<Response<MediaStatusQuery.Data>> {
        val query = MediaStatusQuery(userId = Input.fromNullable(userId), mediaId = Input.fromNullable(mediaId))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaOverview(id: Int): Observable<Response<MediaOverviewQuery.Data>> {
        val query = MediaOverviewQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaCharacters(
        id: Int,
        page: Int
    ): Observable<Response<MediaCharactersQuery.Data>> {
        val query = MediaCharactersQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaStaffs(id: Int, page: Int): Observable<Response<MediaStaffsQuery.Data>> {
        val query = MediaStaffsQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getTrendingMedia(type: MediaType): Observable<Response<TrendingMediaQuery.Data>> {
        val query = TrendingMediaQuery(type = Input.fromNullable(type))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getReleasingToday(page: Int): Observable<Response<ReleasingTodayQuery.Data>> {
        val query = ReleasingTodayQuery(page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}