package com.zen.alchan.data.datasource

import ActivityListQuery
import ActivityQuery
import DeleteActivityMutation
import DeleteActivityReplyMutation
import SaveActivityReplyMutation
import SaveMessageActivityMutation
import SaveTextActivityMutation
import SocialDataQuery
import ToggleActivitySubscriptionMutation
import ToggleLikeMutation
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxMutate
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
        typeIn: List<ActivityType>?,
        isFollowing: Boolean?
    ): Observable<Response<ActivityListQuery.Data>> {
        val query = ActivityListQuery(
            page = Input.fromNullable(page),
            userId = Input.optional(userId),
            typeIn = Input.optional(typeIn),
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

    override fun deleteActivity(id: Int): Completable {
        val mutation = DeleteActivityMutation(id = Input.fromNullable(id))
        return apolloHandler.apolloClient.rxPrefetch(mutation)
    }

    override fun deleteActivityReply(id: Int): Completable {
        val mutation = DeleteActivityReplyMutation(id = Input.fromNullable(id))
        return apolloHandler.apolloClient.rxPrefetch(mutation)
    }

    override fun saveTextActivity(id: Int?, text: String): Observable<Response<SaveTextActivityMutation.Data>> {
        val mutation = SaveTextActivityMutation(id = Input.optional(id), text = Input.fromNullable(text))
        return apolloHandler.apolloClient.rxMutate(mutation).toObservable()
    }

    override fun saveActivityReply(id: Int?, activityId: Int, text: String): Observable<Response<SaveActivityReplyMutation.Data>> {
        val mutation = SaveActivityReplyMutation(id = Input.optional(id), activityId = Input.fromNullable(activityId), text = Input.fromNullable(text))
        return apolloHandler.apolloClient.rxMutate(mutation).toObservable()
    }

    override fun saveMessageActivity(
        id: Int?,
        recipientId: Int,
        message: String,
        private: Boolean
    ): Observable<Response<SaveMessageActivityMutation.Data>> {
        val mutation = SaveMessageActivityMutation(
            id = Input.optional(id),
            recipientId = Input.fromNullable(recipientId),
            message = Input.fromNullable(message),
            private_ = Input.fromNullable(private)
        )
        return apolloHandler.apolloClient.rxMutate(mutation).toObservable()
    }
}