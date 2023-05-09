package com.zen.alchan.data.datasource

import com.apollographql.apollo3.api.ApolloResponse
import com.zen.alchan.*
import com.zen.alchan.data.response.anilist.ListActivityOption
import com.zen.alchan.data.response.anilist.MediaListTypeOptions
import com.zen.alchan.data.response.anilist.NotificationOption
import com.zen.alchan.helper.enums.Favorite
import com.zen.alchan.type.*
import io.reactivex.rxjava3.core.Observable

interface UserDataSource {
    fun getViewerQuery(sort: List<UserStatisticsSort>): Observable<ApolloResponse<ViewerQuery.Data>>
    fun getFollowingAndFollowersCount(userId: Int): Observable<ApolloResponse<FollowingAndFollowersCountQuery.Data>>
    fun getFollowing(userId: Int, page: Int): Observable<ApolloResponse<FollowingQuery.Data>>
    fun getFollowers(userId: Int, page: Int): Observable<ApolloResponse<FollowersQuery.Data>>
    fun toggleFollow(userId: Int): Observable<ApolloResponse<ToggleFollowMutation.Data>>
    fun getUserStatistics(userId: Int, sort: List<UserStatisticsSort>): Observable<ApolloResponse<UserStatisticsQuery.Data>>
    fun getFavorites(userId: Int, page: Int): Observable<ApolloResponse<UserFavouritesQuery.Data>>
    fun updateFavoriteOrder(ids: List<Int>, favorite: Favorite): Observable<ApolloResponse<UpdateFavouriteOrderMutation.Data>>
    fun toggleFavorite(animeId: Int?, mangaId: Int?, characterId: Int?, staffId: Int?, studioId: Int?): Observable<ApolloResponse<ToggleFavouriteMutation.Data>>
    fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        staffNameLanguage: UserStaffNameLanguage,
        activityMergeTime: Int,
        displayAdultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<ApolloResponse<UpdateUserMutation.Data>>
    fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions,
        disabledListActivity: List<ListActivityOption>
    ): Observable<ApolloResponse<UpdateUserMutation.Data>>
    fun updateNotificationsSettings(
        notificationOptions: List<NotificationOption>
    ): Observable<ApolloResponse<UpdateUserMutation.Data>>
    fun getNotifications(page: Int, typeIn: List<NotificationType>?, resetNotificationCount: Boolean): Observable<ApolloResponse<NotificationsQuery.Data>>
    fun getUnreadNotificationCount(): Observable<ApolloResponse<UnreadNotificationCountQuery.Data>>
}