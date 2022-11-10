package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import io.reactivex.Completable
import io.reactivex.Observable
import type.LikeableType

interface SocialDataSource {
    fun getSocialData(): Observable<Response<SocialDataQuery.Data>>
    fun toggleActivitySubscription(id: Int, isSubscribe: Boolean): Completable
    fun toggleLike(id: Int, likeableType: LikeableType): Completable
}