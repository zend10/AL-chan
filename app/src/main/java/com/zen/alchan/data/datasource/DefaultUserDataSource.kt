package com.zen.alchan.data.datasource

import FollowersQuery
import FollowingAndFollowersCountQuery
import FollowingQuery
import UpdateUserMutation
import ViewerQuery
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.rxMutate
import com.apollographql.apollo.rx2.rxQuery
import com.zen.alchan.data.network.apollo.ApolloHandler
import com.zen.alchan.data.response.anilist.MediaListTypeOptions
import com.zen.alchan.data.response.anilist.NotificationOption
import io.reactivex.Observable
import io.reactivex.Single
import type.*

class DefaultUserDataSource(private val apolloHandler: ApolloHandler) : UserDataSource {

    override fun getViewerQuery(sort: List<UserStatisticsSort>): Observable<Response<ViewerQuery.Data>> {
        val query = ViewerQuery(sort = Input.optional(sort))
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getFollowingAndFollowersCount(userId: Int): Observable<Response<FollowingAndFollowersCountQuery.Data>> {
        val query = FollowingAndFollowersCountQuery(userId = userId)
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getFollowing(userId: Int, page: Int): Observable<Response<FollowingQuery.Data>> {
        val query = FollowingQuery(userId = userId, page = Input.fromNullable(page))
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun getFollowers(userId: Int, page: Int): Observable<Response<FollowersQuery.Data>> {
        val query = FollowersQuery(userId = userId, page = Input.fromNullable(page))
        return apolloHandler.apolloClient.rxQuery(query)
    }

    override fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        staffNameLanguage: UserStaffNameLanguage,
        activityMergeTime: Int,
        displayAdultContent: Boolean,
        airingNotifications: Boolean
    ): Single<Response<UpdateUserMutation.Data>> {
        val mutation = UpdateUserMutation(
            titleLanguage = Input.fromNullable(titleLanguage),
            staffNameLanguage = Input.fromNullable(staffNameLanguage),
            activityMergeTime = Input.fromNullable(activityMergeTime),
            displayAdultContent = Input.fromNullable(displayAdultContent),
            airingNotifications = Input.fromNullable(airingNotifications)
        )
        return apolloHandler.apolloClient.rxMutate(mutation)
    }

    override fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions
    ): Single<Response<UpdateUserMutation.Data>> {
        val mutation = UpdateUserMutation(
            scoreFormat = Input.fromNullable(scoreFormat),
            rowOrder = Input.fromNullable(rowOrder),
            animeListOptions = Input.fromNullable(
                MediaListOptionsInput(
                    sectionOrder = Input.fromNullable(animeListOptions.sectionOrder),
                    splitCompletedSectionByFormat = Input.fromNullable(animeListOptions.splitCompletedSectionByFormat),
                    customLists = Input.fromNullable(animeListOptions.customLists),
                    advancedScoring = Input.fromNullable(animeListOptions.advancedScoring),
                    advancedScoringEnabled = Input.fromNullable(animeListOptions.advancedScoringEnabled)
                )
            ),
            mangaListOptions = Input.fromNullable(
                MediaListOptionsInput(
                    sectionOrder = Input.fromNullable(mangaListOptions.sectionOrder),
                    splitCompletedSectionByFormat = Input.fromNullable(mangaListOptions.splitCompletedSectionByFormat),
                    customLists = Input.fromNullable(mangaListOptions.customLists),
                    advancedScoring = Input.fromNullable(mangaListOptions.advancedScoring),
                    advancedScoringEnabled = Input.fromNullable(mangaListOptions.advancedScoringEnabled)
                )
            )
        )
        return apolloHandler.apolloClient.rxMutate(mutation)
    }

    override fun updateNotificationsSettings(notificationOptions: List<NotificationOption>): Single<Response<UpdateUserMutation.Data>> {
        val mutation = UpdateUserMutation(
            notificationOptions = Input.fromNullable(notificationOptions.map {
                NotificationOptionInput(Input.fromNullable(it.type), Input.fromNullable(it.enabled))
            })
        )
        return apolloHandler.apolloClient.rxMutate(mutation)
    }
}