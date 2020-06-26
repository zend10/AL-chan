package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.data.response.NotificationOption
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.enums.FollowPage
import com.zen.alchan.helper.pojo.BestFriend
import io.reactivex.Completable
import type.NotificationType
import type.ScoreFormat
import type.UserTitleLanguage

interface UserRepository {
    val currentUser: User?

    val sessionResponse: LiveData<Boolean>

    val viewerDataResponse: LiveData<Resource<Boolean>>
    val viewerData: LiveData<User?>
    val listOrAniListSettingsChanged: LiveData<Boolean>

    val followersCount: LiveData<Int>
    val followingsCount: LiveData<Int>

    val updateAniListSettingsResponse: LiveData<Resource<Boolean>>
    val updateListSettingsResponse: LiveData<Resource<Boolean>>
    val toggleFavouriteResponse: LiveData<Resource<Boolean>>

    val favoriteAnimeResponse: LiveData<Resource<FavoritesAnimeQuery.Data>>
    val favoriteMangaResponse: LiveData<Resource<FavoritesMangaQuery.Data>>
    val favoriteCharactersResponse: LiveData<Resource<FavoritesCharactersQuery.Data>>
    val favoriteStaffsResponse: LiveData<Resource<FavoritesStaffsQuery.Data>>
    val favoriteStudiosResponse: LiveData<Resource<FavoritesStudiosQuery.Data>>
    val triggerRefreshFavorite: LiveData<Boolean>
    val triggerRefreshReviews: LiveData<Boolean>
    val reorderFavoritesResponse: LiveData<Resource<Boolean>>

    val viewerReviewsResponse: LiveData<Resource<UserReviewsQuery.Data>>

    val userStatisticsResponse: LiveData<Resource<UserStatisticsQuery.Data>>

    val userFollowersResponse: LiveData<Resource<UserFollowersQuery.Data>>
    val userFollowingsResponse: LiveData<Resource<UserFollowingsQuery.Data>>
    val toggleFollowingResponse: LiveData<Resource<ToggleFollowMutation.Data>>
    val toggleFollowerResponse: LiveData<Resource<ToggleFollowMutation.Data>>
    val toggleFollowResponse: LiveData<Resource<ToggleFollowMutation.Data>>

    val viewerDataLastRetrieved: Long?
    val followersCountLastRetrieved: Long?
    val followingsCountLastRetrieved: Long?

    val bestFriends: List<BestFriend>?
    val bestFriendChangedNotifier: LiveData<List<BestFriend>>

    val notificationsResponse: LiveData<Resource<NotificationsQuery.Data>>

    fun checkSession()

    fun getViewerData()
    fun retrieveViewerData()

    fun updateAniListSettings(titleLanguage: UserTitleLanguage, adultContent: Boolean, airingNotifications: Boolean)
    fun updateListSettings(scoreFormat: ScoreFormat, rowOrder: String, animeListOptions: MediaListTypeOptions, mangaListOptions: MediaListTypeOptions)
    fun updateNotificationsSettings(notificationOptions: List<NotificationOption>)
    fun toggleFavourite(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?
    )

    fun getFavoriteAnime(page: Int)
    fun getFavoriteManga(page: Int)
    fun getFavoriteCharacters(page: Int)
    fun getFavoriteStaffs(page: Int)
    fun getFavoriteStudios(page: Int)

    fun triggerRefreshProfilePageChild()

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
    )

    fun getReviews(page: Int)

    fun getStatistics()

    fun getFollowersCount()
    fun getFollowingsCount()

    fun getUserFollowers(page: Int)
    fun getUserFollowings(page: Int)
    fun toggleFollow(userId: Int, fromPage: FollowPage)
    fun toggleFollow(userId: Int)

    fun handleBestFriend(bestFriend: BestFriend, isEdit: Boolean = false)

    fun getNotifications(page: Int, typeIn: List<NotificationType>?, reset: Boolean)
}