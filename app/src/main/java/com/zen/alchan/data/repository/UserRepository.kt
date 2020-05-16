package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaListTypeOptions
import com.zen.alchan.data.response.User
import io.reactivex.Completable
import type.ScoreFormat
import type.UserTitleLanguage

interface UserRepository {
    val viewerDataResponse: LiveData<Resource<Boolean>>
    val viewerData: LiveData<User?>
    val listOrAniListSettingsChanged: LiveData<Boolean>

    val updateAniListSettingsResponse: LiveData<Resource<Boolean>>
    val updateListSettingsResponse: LiveData<Resource<Boolean>>
    val toggleFavouriteResponse: LiveData<Resource<Boolean>>

    val favoriteAnimeResponse: LiveData<Resource<FavoritesAnimeQuery.Data>>
    val favoriteMangaResponse: LiveData<Resource<FavoritesMangaQuery.Data>>
    val favoriteCharactersResponse: LiveData<Resource<FavoritesCharactersQuery.Data>>
    val favoriteStaffsResponse: LiveData<Resource<FavoritesStaffsQuery.Data>>
    val favoriteStudiosResponse: LiveData<Resource<FavoritesStudiosQuery.Data>>
    val triggerRefreshFavorite: LiveData<Boolean>

    val reorderFavoritesResponse: LiveData<Resource<Boolean>>

    val viewerDataLastRetrieved: Long?

    fun getViewerData()
    fun retrieveViewerData()

    fun updateAniListSettings(titleLanguage: UserTitleLanguage, adultContent: Boolean, airingNotifications: Boolean)
    fun updateListSettings(scoreFormat: ScoreFormat, rowOrder: String, animeListOptions: MediaListTypeOptions, mangaListOptions: MediaListTypeOptions)
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
    fun triggerRefreshFavorite()

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
}