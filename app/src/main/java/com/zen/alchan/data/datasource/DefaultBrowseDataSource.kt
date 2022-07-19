package com.zen.alchan.data.datasource

import CharacterQuery
import MediaCharactersQuery
import MediaQuery
import MediaStaffQuery
import StaffQuery
import StudioQuery
import UserQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Observable
import type.*

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

    override fun getCharacterQuery(id: Int, page: Int, sort: List<MediaSort>, type: MediaType?, onList: Boolean?): Observable<Response<CharacterQuery.Data>> {
        val query = CharacterQuery(id = Input.fromNullable(id), page = Input.fromNullable(page), sort = Input.optional(sort), type = Input.optional(type), onList = Input.optional(onList))
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getStaffQuery(
        id: Int,
        page: Int,
        staffMediaSort: List<MediaSort>,
        characterSort: List<CharacterSort>,
        characterMediaSort: List<MediaSort>,
        onList: Boolean?
    ): Observable<Response<StaffQuery.Data>> {
        val query = StaffQuery(
            id = Input.fromNullable(id),
            page = Input.fromNullable(page),
            staffMediaSort = Input.optional(staffMediaSort),
            characterSort = Input.optional(characterSort),
            characterMediaSort = Input.optional(characterMediaSort),
            onList = Input.optional(onList)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getStudioQuery(
        id: Int,
        page: Int,
        sort: List<MediaSort>,
        onList: Boolean?
    ): Observable<Response<StudioQuery.Data>> {
        val query = StudioQuery(
            id = Input.fromNullable(id),
            page = Input.fromNullable(page),
            sort = Input.optional(sort),
            onList = Input.optional(onList)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }
}