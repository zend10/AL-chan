package com.zen.alchan.ui.profile.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.repository.OtherUserRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.pojo.FavoriteItem

class FavoritesViewModel(private val userRepository: UserRepository,
                         private val otherUserRepository: OtherUserRepository,
                         val gson: Gson) : ViewModel() {

    var otherUserId: Int? = null

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

    val triggerRefreshFavorite by lazy {
        userRepository.triggerRefreshFavorite
    }

    val otherUserFavoriteAnimeResponse by lazy {
        otherUserRepository.favoriteAnimeResponse
    }

    val otherUserFavoriteMangaResponse by lazy {
        otherUserRepository.favoriteMangaResponse
    }

    val otherUserFavoriteCharactersResponse by lazy {
        otherUserRepository.favoriteCharactersResponse
    }

    val otherUserFavoriteStaffsResponse by lazy {
        otherUserRepository.favoriteStaffsResponse
    }

    val otherUserFavoriteStudiosResponse by lazy {
        otherUserRepository.favoriteStudiosResponse
    }

    val otherUserTriggerRefreshFavorite by lazy {
        otherUserRepository.triggerRefreshFavorite
    }

    var favoritePageArray = arrayOf(
        BrowsePage.ANIME.name, BrowsePage.MANGA.name, BrowsePage.CHARACTER.name, BrowsePage.STAFF.name, BrowsePage.STUDIO.name
    )

    fun getFavoriteData(favoritePage: BrowsePage): String? {
        return when (favoritePage) {
            BrowsePage.ANIME -> gson.toJson(animeList)
            BrowsePage.MANGA -> gson.toJson(mangaList)
            BrowsePage.CHARACTER -> gson.toJson(charactersList)
            BrowsePage.STAFF -> gson.toJson(staffsList)
            BrowsePage.STUDIO -> gson.toJson(studiosList)
            else -> null
        }
    }

    fun getFavorites(browsePage: BrowsePage) {
        if (otherUserId != null) {
            when (browsePage) {
                BrowsePage.ANIME -> otherUserRepository.getFavoriteAnime(otherUserId!!, animePage)
                BrowsePage.MANGA -> otherUserRepository.getFavoriteManga(otherUserId!!, mangaPage)
                BrowsePage.CHARACTER -> otherUserRepository.getFavoriteCharacters(otherUserId!!, charactersPage)
                BrowsePage.STAFF -> otherUserRepository.getFavoriteStaffs(otherUserId!!, staffsPage)
                BrowsePage.STUDIO -> otherUserRepository.getFavoriteStudios(otherUserId!!, studiosPage)
            }
        } else {
            when (browsePage) {
                BrowsePage.ANIME -> userRepository.getFavoriteAnime(animePage)
                BrowsePage.MANGA -> userRepository.getFavoriteManga(mangaPage)
                BrowsePage.CHARACTER -> userRepository.getFavoriteCharacters(charactersPage)
                BrowsePage.STAFF -> userRepository.getFavoriteStaffs(staffsPage)
                BrowsePage.STUDIO -> userRepository.getFavoriteStudios(studiosPage)
            }
        }
    }

    fun refresh() {
        animePage = 1
        animeList.clear()
        animeHasNextPage = true
        getFavorites(BrowsePage.ANIME)

        mangaPage = 1
        mangaList.clear()
        mangaHasNextPage = true
        getFavorites(BrowsePage.MANGA)

        charactersPage = 1
        charactersList.clear()
        charactersHasNextPage = true
        getFavorites(BrowsePage.CHARACTER)

        staffsPage = 1
        staffsList.clear()
        staffsHasNextPage = true
        getFavorites(BrowsePage.STAFF)

        studiosPage = 1
        studiosList.clear()
        studiosHasNextPage = true
        getFavorites(BrowsePage.STUDIO)
    }

    fun getAnimeObserver(): LiveData<Resource<FavoritesAnimeQuery.Data>> {
        return if (otherUserId != null) {
            otherUserFavoriteAnimeResponse
        } else {
            favoriteAnimeResponse
        }
    }

    fun getMangaObserver(): LiveData<Resource<FavoritesMangaQuery.Data>> {
        return if (otherUserId != null) {
            otherUserFavoriteMangaResponse
        } else {
            favoriteMangaResponse
        }
    }

    fun getCharactersObserver(): LiveData<Resource<FavoritesCharactersQuery.Data>> {
        return if (otherUserId != null) {
            otherUserFavoriteCharactersResponse
        } else {
            favoriteCharactersResponse
        }
    }

    fun getStaffObserver(): LiveData<Resource<FavoritesStaffsQuery.Data>> {
        return if (otherUserId != null) {
            otherUserFavoriteStaffsResponse
        } else {
            favoriteStaffsResponse
        }
    }

    fun getStudiosObserver(): LiveData<Resource<FavoritesStudiosQuery.Data>> {
        return if (otherUserId != null) {
            otherUserFavoriteStudiosResponse
        } else {
            favoriteStudiosResponse
        }
    }

    fun getTriggerRefreshObserver(): LiveData<Boolean> {
        return if (otherUserId != null) {
            otherUserTriggerRefreshFavorite
        } else {
            triggerRefreshFavorite
        }
    }

    fun getMixedList(): ArrayList<FavoriteItem> {
        val mixedList = ArrayList<FavoriteItem>()
        if (!animeList.isNullOrEmpty()) {
            mixedList.add(FavoriteItem(null, null, null, 0, BrowsePage.ANIME))
            mixedList.addAll(animeList)
        }
        if (!mangaList.isNullOrEmpty()) {
            mixedList.add(FavoriteItem(null, null, null, 0, BrowsePage.MANGA))
            mixedList.addAll(mangaList)
        }
        if (!charactersList.isNullOrEmpty()) {
            mixedList.add(FavoriteItem(null, null, null, 0, BrowsePage.CHARACTER))
            mixedList.addAll(charactersList)
        }
        if (!staffsList.isNullOrEmpty()) {
            mixedList.add(FavoriteItem(null, null, null, 0, BrowsePage.STAFF))
            mixedList.addAll(staffsList)
        }
        return mixedList
    }
}