package com.zen.alchan.data.datasource

import CharacterQuery
import MediaQuery
import StaffQuery
import UserQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable
import type.UserStatisticsSort

class DefaultBrowseDataSource(private val apolloHandler: ApolloHandler) : BrowseDataSource {

    override fun getUserQuery(
        id: Int,
        sort: List<UserStatisticsSort>
    ): Observable<Response<UserQuery.Data>> {
        val query = UserQuery(id = Input.fromNullable(id), sort = Input.optional(sort))
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getMediaQuery(id: Int): Observable<Response<MediaQuery.Data>> {
        val query = MediaQuery(id = Input.fromNullable(id))
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getCharacterQuery(id: Int): Observable<Response<CharacterQuery.Data>> {
        val query = CharacterQuery(id = Input.fromNullable(id))
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getStaffQuery(id: Int): Observable<Response<StaffQuery.Data>> {
        val query = StaffQuery(id = Input.fromNullable(id))
        return apolloHandler.apolloClient.rxQuery(query)
    }
}