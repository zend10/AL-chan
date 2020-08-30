package com.zen.alchan.data.datasource

import CharacterIsFavoriteQuery
import CharacterMediaConnectionQuery
import CharacterQuery
import IdFromNameQuery
import StaffBioQuery
import StaffCharacterConnectionQuery
import StaffIsFavoriteQuery
import StaffMediaConnectionQuery
import StaffQuery
import StudioIsFavoriteQuery
import StudioMediaConnectionQuery
import StudioQuery
import com.apollographql.apollo.api.Input
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
        val query = CharacterQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getCharacterMedia(
        id: Int,
        page: Int
    ): Observable<Response<CharacterMediaConnectionQuery.Data>> {
        val query = CharacterMediaConnectionQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkCharacterIsFavorite(id: Int): Observable<Response<CharacterIsFavoriteQuery.Data>> {
        val query = CharacterIsFavoriteQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStaff(id: Int): Observable<Response<StaffQuery.Data>> {
        val query = StaffQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStaffBio(id: Int): Observable<Response<StaffBioQuery.Data>> {
        val query = StaffBioQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStaffCharacter(
        id: Int,
        page: Int
    ): Observable<Response<StaffCharacterConnectionQuery.Data>> {
        val query = StaffCharacterConnectionQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
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
        val query = StaffMediaConnectionQuery(id = Input.fromNullable(id), type = Input.fromNullable(type), page = Input.fromNullable(page))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkStaffIsFavorite(id: Int): Observable<Response<StaffIsFavoriteQuery.Data>> {
        val query = StaffIsFavoriteQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getStudio(id: Int): Observable<Response<StudioQuery.Data>> {
        val query = StudioQuery(id = Input.fromNullable(id))
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
        val query = StudioMediaConnectionQuery(id = Input.fromNullable(id), page = Input.fromNullable(page), sort = Input.fromNullable(sort))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkStudioIsFavorite(id: Int): Observable<Response<StudioIsFavoriteQuery.Data>> {
        val query = StudioIsFavoriteQuery(id = Input.fromNullable(id))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getIdFromName(name: String): Observable<Response<IdFromNameQuery.Data>> {
        val query = IdFromNameQuery(name = Input.fromNullable(name))
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}