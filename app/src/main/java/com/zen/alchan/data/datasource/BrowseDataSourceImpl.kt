package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import type.MediaSort
import type.MediaType

class BrowseDataSourceImpl(private val apolloHandler: ApolloHandler) : BrowseDataSource {

    override fun getCharacter(id: Int): Observable<Response<CharacterQuery.Data>> {
        val query = CharacterQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getCharacterMedia(
        id: Int,
        page: Int
    ): Observable<Response<CharacterMediaConnectionQuery.Data>> {
        val query = CharacterMediaConnectionQuery.builder().id(id).page(page).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkCharacterIsFavorite(id: Int): Observable<Response<CharacterIsFavoriteQuery.Data>> {
        val query = CharacterIsFavoriteQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStaff(id: Int): Observable<Response<StaffQuery.Data>> {
        val query = StaffQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStaffBio(id: Int): Observable<Response<StaffBioQuery.Data>> {
        val query = StaffBioQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStaffCharacter(
        id: Int,
        page: Int
    ): Observable<Response<StaffCharacterConnectionQuery.Data>> {
        val query = StaffCharacterConnectionQuery.builder().id(id).page(page).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStaffMedia(
        id: Int,
        type: MediaType,
        page: Int
    ): Observable<Response<StaffMediaConnectionQuery.Data>> {
        val query = StaffMediaConnectionQuery.builder().id(id).type(type).page(page).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkStaffIsFavorite(id: Int): Observable<Response<StaffIsFavoriteQuery.Data>> {
        val query = StaffIsFavoriteQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStudio(id: Int): Observable<Response<StudioQuery.Data>> {
        val query = StudioQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStudioMedia(
        id: Int,
        page: Int,
        sort: MediaSort
    ): Observable<Response<StudioMediaConnectionQuery.Data>> {
        val query = StudioMediaConnectionQuery.builder().id(id).page(page).sort(sort).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkStudioIsFavorite(id: Int): Observable<Response<StudioIsFavoriteQuery.Data>> {
        val query = StudioIsFavoriteQuery.builder().id(id).build()
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}