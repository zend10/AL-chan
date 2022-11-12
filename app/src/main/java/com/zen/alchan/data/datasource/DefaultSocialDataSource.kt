package com.zen.alchan.data.datasource

import ActivityListQuery
import ActivityQuery
import SocialDataQuery
import ToggleActivitySubscriptionMutation
import ToggleLikeMutation
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxPrefetch
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import io.reactivex.Completable
import io.reactivex.Observable
import type.ActivityType
import type.LikeableType

class DefaultSocialDataSource(private val apolloHandler: ApolloHandler) : SocialDataSource {

    override fun getSocialData(): Observable<Response<SocialDataQuery.Data>> {
        val query = SocialDataQuery()
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getActivityDetail(id: Int): Observable<Response<ActivityQuery.Data>> {
        val query = ActivityQuery(id = Input.fromNullable(id))
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getActivityList(
        page: Int,
        userId: Int?,
        type: ActivityType?,
        isFollowing: Boolean?
    ): Observable<Response<ActivityListQuery.Data>> {
        val query = ActivityListQuery(
            page = Input.fromNullable(page),
            userId = Input.optional(userId),
            type = Input.optional(type),
            isFollowing = Input.optional(isFollowing)
        )
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun toggleActivitySubscription(id: Int, isSubscribe: Boolean): Completable {
        val mutation = ToggleActivitySubscriptionMutation(activityId = Input.fromNullable(id), subscribe = Input.fromNullable(isSubscribe))
        return apolloHandler.apolloClient.rxPrefetch(mutation)
    }

    override fun toggleLike(id: Int, likeableType: LikeableType): Completable {
        val mutation = ToggleLikeMutation(id = Input.fromNullable(id), likeableType = Input.fromNullable(likeableType))
        return apolloHandler.apolloClient.rxPrefetch(mutation)
    }
}