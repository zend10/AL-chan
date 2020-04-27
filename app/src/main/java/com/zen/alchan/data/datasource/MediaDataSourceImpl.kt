package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MediaDataSourceImpl(private val apolloHandler: ApolloHandler) : MediaDataSource {

    override fun getGenre(): Observable<Response<GenreQuery.Data>> {
        val query = GenreQuery.builder().build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMedia(id: Int): Observable<Response<MediaQuery.Data>> {
        val query = MediaQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkMediaStatus(
        userId: Int,
        mediaId: Int
    ): Observable<Response<MediaStatusQuery.Data>> {
        val query = MediaStatusQuery.builder().userId(userId).mediaId(mediaId).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaOverview(id: Int): Observable<Response<MediaOverviewQuery.Data>> {
        val query = MediaOverviewQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaCharacters(
        id: Int,
        page: Int
    ): Observable<Response<MediaCharactersQuery.Data>> {
        val query = MediaCharactersQuery.builder().id(id).page(page).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getMediaStaffs(id: Int, page: Int): Observable<Response<MediaStaffsQuery.Data>> {
        val query = MediaStaffsQuery.builder().id(id).page(page).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}