package com.zen.alchan.data.datasource

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.rx3.rxSingle
import com.zen.alchan.*
import com.zen.alchan.data.network.apollo.ApolloHandler
import com.zen.alchan.type.ActivityType
import com.zen.alchan.type.LikeableType
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class DefaultSocialDataSource(private val apolloHandler: ApolloHandler) : SocialDataSource {

    override fun getSocialData(): Observable<ApolloResponse<SocialDataQuery.Data>> {
        val query = SocialDataQuery()
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getActivityDetail(id: Int): Observable<ApolloResponse<ActivityQuery.Data>> {
        val query = ActivityQuery(id = Optional.present(id))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getActivityList(
        page: Int,
        userId: Int?,
        typeIn: List<ActivityType>?,
        isFollowing: Boolean?
    ): Observable<ApolloResponse<ActivityListQuery.Data>> {
        val query = ActivityListQuery(
            page = Optional.present(page),
            userId = Optional.presentIfNotNull(userId),
            typeIn = Optional.presentIfNotNull(typeIn),
            isFollowing = Optional.presentIfNotNull(isFollowing)
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun toggleActivitySubscription(id: Int, isSubscribe: Boolean): Completable {
        val mutation = ToggleActivitySubscriptionMutation(activityId = Optional.present(id), subscribe = Optional.present(isSubscribe))
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().ignoreElement()
    }

    override fun toggleLike(id: Int, likeableType: LikeableType): Completable {
        val mutation = ToggleLikeMutation(id = Optional.present(id), likeableType = Optional.present(likeableType))
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().ignoreElement()
    }

    override fun deleteActivity(id: Int): Completable {
        val mutation = DeleteActivityMutation(id = Optional.present(id))
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().ignoreElement()
    }

    override fun deleteActivityReply(id: Int): Completable {
        val mutation = DeleteActivityReplyMutation(id = Optional.present(id))
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().ignoreElement()
    }

    override fun saveTextActivity(id: Int?, text: String): Observable<ApolloResponse<SaveTextActivityMutation.Data>> {
        val mutation = SaveTextActivityMutation(id = Optional.presentIfNotNull(id), text = Optional.present(text))
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().toObservable()
    }

    override fun saveActivityReply(id: Int?, activityId: Int, text: String): Observable<ApolloResponse<SaveActivityReplyMutation.Data>> {
        val mutation = SaveActivityReplyMutation(id = Optional.presentIfNotNull(id), activityId = Optional.present(activityId), text = Optional.present(text))
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().toObservable()
    }

    override fun saveMessageActivity(
        id: Int?,
        recipientId: Int,
        message: String,
        private: Boolean
    ): Observable<ApolloResponse<SaveMessageActivityMutation.Data>> {
        val mutation = SaveMessageActivityMutation(
            id = Optional.presentIfNotNull(id),
            recipientId = Optional.present(recipientId),
            message = Optional.present(message),
            private = Optional.present(private)
        )
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().toObservable()
    }
}