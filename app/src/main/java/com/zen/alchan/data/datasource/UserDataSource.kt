package com.zen.alchan.data.datasource

import ViewerQuery
import com.apollographql.apollo.api.Response
import com.zen.alchan.data.response.MediaListOptions
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.data.response.NotificationOption
import io.reactivex.Completable
import io.reactivex.Observable
import type.MediaType
import type.ScoreFormat
import type.UserTitleLanguage

interface UserDataSource {
    fun checkSession(): Observable<Response<SessionQuery.Data>>

    fun getViewerData(): Observable<Response<ViewerQuery.Data>>

    fun updateAniListSettings(
        titleLanguage: UserTitleLanguage,
        adultContent: Boolean,
        airingNotifications: Boolean
    ): Observable<Response<AniListSettingsMutation.Data>>

    fun updateListSettings(
        scoreFormat: ScoreFormat,
        rowOrder: String,
        animeListOptions: MediaListTypeOptions,
        mangaListOptions: MediaListTypeOptions
    ): Observable<Response<ListSettingsMutation.Data>>

    fun updateNotificationsSettings(
        notificationOptions: List<NotificationOption>
    ): Observable<Response<AniListSettingsMutation.Data>>

    fun toggleFavourite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?
    ): Completable

    fun getFavoriteAnime(id: Int, page: Int): Observable<Response<FavoritesAnimeQuery.Data>>
    fun getFavoriteManga(id: Int, page: Int): Observable<Response<FavoritesMangaQuery.Data>>
    fun getFavoriteCharacters(id: Int, page: Int): Observable<Response<FavoritesCharactersQuery.Data>>
    fun getFavoriteStaffs(id: Int, page: Int): Observable<Response<FavoritesStaffsQuery.Data>>
    fun getFavoriteStudios(id: Int, page: Int): Observable<Response<FavoritesStudiosQuery.Data>>

    fun reorderFavorites(
        animeIds: List<Int>?,
        mangaIds: List<Int>?,
        characterIds: List<Int>?,
        staffIds: List<Int>?,
        studioIds: List<Int>?,
        animeOrder: List<Int>?,
        mangaOrder: List<Int>?,
        characterOrder: List<Int>?,
        staffOrder: List<Int>?,
        studioOrder: List<Int>?
    ): Completable

    fun getReviews(userId: Int, page: Int): Observable<Response<UserReviewsQuery.Data>>

    fun getStatistics(userId: Int): Observable<Response<UserStatisticsQuery.Data>>

    fun getFollowers(userId: Int, page: Int): Observable<Response<UserFollowersQuery.Data>>
    fun getFollowings(userId: Int, page: Int): Observable<Response<UserFollowingsQuery.Data>>

    fun toggleFollow(userId: Int): Observable<Response<ToggleFollowMutation.Data>>

    fun getUserData(id: Int): Observable<Response<UserQuery.Data>>
    fun getUserMediaCollection(userId: Int, type: MediaType): Observable<Response<UserMediaListCollectionQuery.Data>>
}