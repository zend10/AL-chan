package com.zen.alchan.data.datasource

import CharacterQuery
import MediaCharactersQuery
import MediaQuery
import MediaStaffQuery
import StaffQuery
import UserQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable
import type.StaffLanguage
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

    override fun getMediaCharactersQuery(
        id: Int,
        page: Int,
        language: StaffLanguage
    ): Observable<Response<MediaCharactersQuery.Data>> {
        val query = MediaCharactersQuery(id = Input.fromNullable(id), page = Input.fromNullable(page), language = Input.fromNullable(language))
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getMediaStaffQuery(
        id: Int,
        page: Int
    ): Observable<Response<MediaStaffQuery.Data>> {
        val query = MediaStaffQuery(id = Input.fromNullable(id), page = Input.fromNullable(page))
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