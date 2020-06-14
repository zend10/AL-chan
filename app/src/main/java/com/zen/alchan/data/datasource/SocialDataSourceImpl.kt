package com.zen.alchan.data.datasource

import ActivityQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.zen.alchan.data.network.ApolloHandler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import type.ActivityType

class SocialDataSourceImpl(private val apolloHandler: ApolloHandler) : SocialDataSource {

    override fun getFriendsActivity(
        typeIn: List<ActivityType>?,
        userIdIn: List<Int>?
    ): Observable<Response<ActivityQuery.Data>> {
        val query = ActivityQuery(
            page = Input.fromNullable(1),
            perPage = Input.fromNullable(10),
            type_in = Input.optional(typeIn),
            userId_in = Input.optional(userIdIn),
            isFollowing = Input.optional(if (userIdIn != null) null else true)
        )
        val queryCall = apolloHandler.apolloClient.query(query)
        return Rx2Apollo.from(queryCall)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}