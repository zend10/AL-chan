package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Observable
import type.ActivityType

interface SocialDataSource {
    fun getFriendsActivity(
        typeIn: List<ActivityType>?,
        userIdIn: List<Int>?,
        userIdNotIn: List<Int>?
    ): Observable<Response<ActivityQuery.Data>>

    fun getActivityDetail(
        id: Int
    ): Observable<Response<ActivityDetailQuery.Data>>
}