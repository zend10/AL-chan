package com.zen.alchan.ui.profile.favorites

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.pojo.FavoriteItem

class FavoritesViewModel(private val userRepository: UserRepository,
                         val gson: Gson) : ViewModel() {

    var animePage = 1
    var animeHasNextPage = true
    var animeIsInit = false
    var animeList = ArrayList<FavoriteItem>()

    var mangaPage = 1
    var mangaHasNextPage = true
    var mangaIsInit = false
    var mangaList = ArrayList<FavoriteItem>()

    var charactersPage = 1
    var charactersHasNextPage = true
    var charactersIsInit = false
    var charactersList = ArrayList<FavoriteItem>()

    var staffsPage = 1
    var staffsHasNextPage = true
    var staffsIsInit = false
    var staffsList = ArrayList<FavoriteItem>()

    var studiosPage = 1
    var studiosHasNextPage = true
    var studiosIsInit = false
    var studiosList = ArrayList<FavoriteItem>()

    val favoriteAnimeResponse by lazy {
        userRepository.favoriteAnimeResponse
    }

    val favoriteMangaResponse by lazy {
        userRepository.favoriteMangaResponse
    }

    val favoriteCharactersResponse by lazy {
        userRepository.favoriteCharactersResponse
    }

    val favoriteStaffsResponse by lazy {
        userRepository.favoriteStaffsResponse
    }

    val favoriteStudiosResponse by lazy {
        userRepository.favoriteStudiosResponse
    }

    fun getFavorites(browsePage: BrowsePage) {
        when (browsePage) {
            BrowsePage.ANIME -> userRepository.getFavoriteAnime(animePage)
            BrowsePage.MANGA -> userRepository.getFavoriteManga(mangaPage)
            BrowsePage.CHARACTER -> userRepository.getFavoriteCharacters(charactersPage)
            BrowsePage.STAFF -> userRepository.getFavoriteStaffs(staffsPage)
            BrowsePage.STUDIO -> userRepository.getFavoriteStudios(studiosPage)
        }
    }
}