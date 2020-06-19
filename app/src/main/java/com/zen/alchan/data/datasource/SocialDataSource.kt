package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Completable
import io.reactivex.Observable
import type.ActivityType
import type.LikeableType

interface SocialDataSource {
    fun getFriendsActivity(
        typeIn: List<ActivityType>?,
        userId: Int?,
        userIdNotIn: List<Int>?
    ): Observable<Response<ActivityQuery.Data>>

    fun getActivityDetail(
        id: Int
    ): Observable<Response<ActivityDetailQuery.Data>>

    fun toggleLike(
        id: Int,
        type: LikeableType
    ): Observable<Response<ToggleLikeMutation.Data>>

    fun toggleActivitySubscription(
        activityId: Int,
        subscribe: Boolean
    ): Completable

    fun deleteActivity(
        id: Int
    ): Completable

    fun deleteActivityReply(
        id: Int
    ): Completable

    fun getActivities(
        page: Int,
        typeIn: List<ActivityType>?,
        userId: Int?
    ): Observable<Response<ActivityQuery.Data>>
}