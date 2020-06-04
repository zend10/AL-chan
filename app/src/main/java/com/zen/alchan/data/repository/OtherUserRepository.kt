package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import type.MediaType

interface OtherUserRepository {
    val userDataResponse: LiveData<Resource<Boolean>>
    val userData: LiveData<UserQuery.Data>
    val followersCount: LiveData<Int>
    val followingsCount: LiveData<Int>

    val userMediaListCollection: LiveData<Resource<UserMediaListCollectionQuery.Data>>
    val userFollowersResponse: LiveData<Resource<UserFollowersQuery.Data>>
    val userFollowingsResponse: LiveData<Resource<UserFollowingsQuery.Data>>

    val triggerRefreshFavorite: LiveData<Boolean>
    val favoriteAnimeResponse: LiveData<Resource<FavoritesAnimeQuery.Data>>
    val favoriteMangaResponse: LiveData<Resource<FavoritesMangaQuery.Data>>
    val favoriteCharactersResponse: LiveData<Resource<FavoritesCharactersQuery.Data>>
    val favoriteStaffsResponse: LiveData<Resource<FavoritesStaffsQuery.Data>>
    val favoriteStudiosResponse: LiveData<Resource<FavoritesStudiosQuery.Data>>

    val userStatisticsResponse: LiveData<Resource<UserStatisticsQuery.Data>>

    val triggerRefreshReviews: LiveData<Boolean>
    val userReviewsResponse: LiveData<Resource<UserReviewsQuery.Data>>

    fun retrieveUserData(userId: Int)
    fun getFollowersCount(userId: Int)
    fun getFollowingsCount(userId: Int)

    fun getUserMediaListCollection(userId: Int, type: MediaType)
    fun getUserFollowers(userId: Int, page: Int)
    fun getUserFollowings(userId: Int, page: Int)

    fun triggerRefreshProfilePageChild(userId: Int)

    fun getFavoriteAnime(userId: Int, page: Int)
    fun getFavoriteManga(userId: Int, page: Int)
    fun getFavoriteCharacters(userId: Int, page: Int)
    fun getFavoriteStaffs(userId: Int, page: Int)
    fun getFavoriteStudios(userId: Int, page: Int)
    fun getStatistics(userId: Int)
    fun getReviews(userId: Int, page: Int)
}