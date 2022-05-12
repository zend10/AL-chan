package com.zen.alchan.data.datasource

import com.apollographql.apollo.api.Response
import com.zen.alchan.data.response.anilist.MediaListTypeOptions
import com.zen.alchan.data.response.anilist.NotificationOption
import com.zen.alchan.helper.enums.Favorite
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import type.ScoreFormat
import type.UserStaffNameLanguage
import type.UserStatisticsSort
import type.UserTitleLanguage

interface UserDataSource {
    fun getViewerQuery(sort: List<UserStatisticsSort>): Observable<Response<ViewerQuery.Data>>
    fun getFollowingAndFollowersCount(userId: Int): Observable<Response<FollowingAndFollowersCountQuery.Data>>
    fun getFollowing(userId: Int, page: Int): Observable<Response<FollowingQuery.Data>>
    fun getFollowers(userId: Int, page: Int): Observable<Response<FollowersQuery.Data>>
    fun toggleFollow(userId: Int): Single<Response<ToggleFollowMutation.Data>>
    fun getUserStatistics(userId: Int, sort: List<UserStatisticsSort>): Observable<Response<UserStatisticsQuery.Data>>
    fun getFavorites(userId: Int, page: Int): Observable<Response<UserFavouritesQuery.Data>>
    fun updateFavoriteOrder(ids: List<Int>, favorite: Favorite): Single<Response<UpdateFavouriteOrderMutation.Data>>
    fun toggleFavorite(animeId: Int?, mangaId: Int?, characterId: Int?, staffId: Int?, studioId: Int?): Single<Response<ToggleFavouriteMutation.Data>>
    fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        staffNameLanguage: UserStaffNameLanguage,
        activityMergeTime: Int,
        displayAdultContent: Boolean,
        airingNotifications: Boolean
    ): Single<Response<UpdateUserMutation.Data>>
    fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions
    ): Single<Response<UpdateUserMutation.Data>>
    fun updateNotificationsSettings(
        notificationOptions: List<NotificationOption>
    ): Single<Response<UpdateUserMutation.Data>>
}