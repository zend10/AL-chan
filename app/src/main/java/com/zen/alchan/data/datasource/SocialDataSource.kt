package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Completable
import io.reactivex.Observable
import type.ActivityType
import type.LikeableType

interface SocialDataSource {
    fun getSocialData(): Observable<Response<SocialDataQuery.Data>>
    fun getActivityDetail(id: Int): Observable<Response<ActivityQuery.Data>>
    fun getActivityList(page: Int, userId: Int?, type: ActivityType?, isFollowing: Boolean?): Observable<Response<ActivityListQuery.Data>>
    fun toggleActivitySubscription(id: Int, isSubscribe: Boolean): Completable
    fun toggleLike(id: Int, likeableType: LikeableType): Completable
}