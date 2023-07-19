package com.zen.alchan.data.datasource

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.rx3.rxSingle
import com.zen.alchan.*
import com.zen.alchan.data.network.apollo.ApolloHandler
import com.zen.alchan.data.response.anilist.ListActivityOption
import com.zen.alchan.data.response.anilist.MediaListTypeOptions
import com.zen.alchan.data.response.anilist.NotificationOption
import com.zen.alchan.helper.enums.Favorite
import com.zen.alchan.type.*
import io.reactivex.rxjava3.core.Observable

class DefaultUserDataSource(private val apolloHandler: ApolloHandler) : UserDataSource {

    override fun getViewerQuery(sort: List<UserStatisticsSort>): Observable<ApolloResponse<ViewerQuery.Data>> {
        val query = ViewerQuery(sort = Optional.presentIfNotNull(sort))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getFollowingAndFollowersCount(userId: Int): Observable<ApolloResponse<FollowingAndFollowersCountQuery.Data>> {
        val query = FollowingAndFollowersCountQuery(userId = userId)
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getFollowing(userId: Int, page: Int): Observable<ApolloResponse<FollowingQuery.Data>> {
        val query = FollowingQuery(userId = userId, page = Optional.present(page))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getFollowers(userId: Int, page: Int): Observable<ApolloResponse<FollowersQuery.Data>> {
        val query = FollowersQuery(userId = userId, page = Optional.present(page))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun toggleFollow(userId: Int): Observable<ApolloResponse<ToggleFollowMutation.Data>> {
        val mutation = ToggleFollowMutation(Optional.present(userId))
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().toObservable()
    }

    override fun getUserStatistics(
        userId: Int,
        sort: List<UserStatisticsSort>
    ): Observable<ApolloResponse<UserStatisticsQuery.Data>> {
        val query = UserStatisticsQuery(Optional.present(userId), Optional.present(sort))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getFavorites(
        userId: Int,
        page: Int
    ): Observable<ApolloResponse<UserFavouritesQuery.Data>> {
        val query = UserFavouritesQuery(Optional.present(userId), Optional.present(page))
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun updateFavoriteOrder(
        ids: List<Int>,
        favorite: Favorite
    ): Observable<ApolloResponse<UpdateFavouriteOrderMutation.Data>> {
        val order = ids.mapIndexed { index, _ -> index + 1 }
        val mutation = UpdateFavouriteOrderMutation(
            animeIds = Optional.presentIfNotNull(if (favorite == Favorite.ANIME) ids else null),
            mangaIds = Optional.presentIfNotNull(if (favorite == Favorite.MANGA) ids else null),
            characterIds = Optional.presentIfNotNull(if (favorite == Favorite.CHARACTERS) ids else null),
            staffIds = Optional.presentIfNotNull(if (favorite == Favorite.STAFF) ids else null),
            studioIds = Optional.presentIfNotNull(if (favorite == Favorite.STUDIOS) ids else null),
            animeOrder = Optional.presentIfNotNull(if (favorite == Favorite.ANIME) order else null),
            mangaOrder = Optional.presentIfNotNull(if (favorite == Favorite.MANGA) order else null),
            characterOrder = Optional.presentIfNotNull(if (favorite == Favorite.CHARACTERS) order else null),
            staffOrder = Optional.presentIfNotNull(if (favorite == Favorite.STAFF) order else null),
            studioOrder = Optional.presentIfNotNull(if (favorite == Favorite.STUDIOS) order else null)
        )
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().toObservable()
    }

    override fun toggleFavorite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?
    ): Observable<ApolloResponse<ToggleFavouriteMutation.Data>> {
        val mutation = ToggleFavouriteMutation(
            animeId = Optional.presentIfNotNull(animeId),
            mangaId = Optional.presentIfNotNull(mangaId),
            characterId = Optional.presentIfNotNull(characterId),
            staffId = Optional.presentIfNotNull(staffId),
            studioId = Optional.presentIfNotNull(studioId)
        )
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().toObservable()
    }

    override fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        staffNameLanguage: UserStaffNameLanguage,
        activityMergeTime: Int,
        displayAdultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<ApolloResponse<UpdateUserMutation.Data>> {
        val mutation = UpdateUserMutation(
            titleLanguage = Optional.present(titleLanguage),
            staffNameLanguage = Optional.present(staffNameLanguage),
            activityMergeTime = Optional.present(activityMergeTime),
            displayAdultContent = Optional.present(displayAdultContent),
            airingNotifications = Optional.present(airingNotifications)
        )
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().toObservable()
    }

    override fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions,
        disabledListActivity: List<ListActivityOption>
    ): Observable<ApolloResponse<UpdateUserMutation.Data>> {
        val mutation = UpdateUserMutation(
            scoreFormat = Optional.present(scoreFormat),
            rowOrder = Optional.present(rowOrder),
            animeListOptions = Optional.present(
                MediaListOptionsInput(
                    sectionOrder = Optional.present(animeListOptions.sectionOrder),
                    splitCompletedSectionByFormat = Optional.present(animeListOptions.splitCompletedSectionByFormat),
                    customLists = Optional.present(animeListOptions.customLists),
                    advancedScoring = Optional.present(animeListOptions.advancedScoring),
                    advancedScoringEnabled = Optional.present(animeListOptions.advancedScoringEnabled)
                )
            ),
            mangaListOptions = Optional.present(
                MediaListOptionsInput(
                    sectionOrder = Optional.present(mangaListOptions.sectionOrder),
                    splitCompletedSectionByFormat = Optional.present(mangaListOptions.splitCompletedSectionByFormat),
                    customLists = Optional.present(mangaListOptions.customLists),
                    advancedScoring = Optional.present(mangaListOptions.advancedScoring),
                    advancedScoringEnabled = Optional.present(mangaListOptions.advancedScoringEnabled)
                )
            ),
            disabledListActivity = Optional.present(
                disabledListActivity.map {
                    ListActivityOptionInput(
                        disabled = Optional.present(it.disabled),
                        type = Optional.present(it.type)
                    )
                }
            )
        )
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().toObservable()
    }

    override fun updateNotificationsSettings(notificationOptions: List<NotificationOption>): Observable<ApolloResponse<UpdateUserMutation.Data>> {
        val mutation = UpdateUserMutation(
            notificationOptions = Optional.present(notificationOptions.map {
                NotificationOptionInput(Optional.present(it.type), Optional.present(it.enabled))
            })
        )
        return apolloHandler.apolloClient.mutation(mutation).rxSingle().toObservable()
    }

    override fun getNotifications(
        page: Int,
        typeIn: List<NotificationType>?,
        resetNotificationCount: Boolean
    ): Observable<ApolloResponse<NotificationsQuery.Data>> {
        val query = NotificationsQuery(
            page = Optional.present(page),
            type_in = Optional.presentIfNotNull(typeIn),
            resetNotificationCount = Optional.present(resetNotificationCount)
        )
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }

    override fun getUnreadNotificationCount(): Observable<ApolloResponse<UnreadNotificationCountQuery.Data>> {
        val query = UnreadNotificationCountQuery()
        return apolloHandler.apolloClient.query(query).rxSingle().toObservable()
    }
}