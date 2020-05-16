package com.zen.alchan.ui.profile.favorites.reorder

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.pojo.FavoriteItem

class ReorderFavoritesViewModel(private val userRepository: UserRepository,
                                val gson: Gson): ViewModel() {

    var favoriteList = ArrayList<FavoriteItem>()
    var favoriteCategory: BrowsePage? = null

    val reorderFavoritesResponse by lazy {
        userRepository.reorderFavoritesResponse
    }

    fun saveOrder() {
        if (favoriteList.isNullOrEmpty()) {
            return
        }

        var animeIds: ArrayList<Int>? = null
        var mangaIds: ArrayList<Int>? = null
        var characterIds: ArrayList<Int>? = null
        var staffIds: ArrayList<Int>? = null
        var studioIds: ArrayList<Int>? = null
        var animeOrder: ArrayList<Int>? = null
        var mangaOrder: ArrayList<Int>? = null
        var characterOrder: ArrayList<Int>? = null
        var staffOrder: ArrayList<Int>? = null
        var studioOrder: ArrayList<Int>? = null

        val idList = ArrayList<Int>()
        val orderList = ArrayList<Int>()
        favoriteList.forEach {
            idList.add(it.id)
            orderList.add(idList.size)
        }

        when (favoriteCategory) {
            BrowsePage.ANIME -> { animeIds = idList; animeOrder = orderList; }
            BrowsePage.MANGA -> { mangaIds = idList; mangaOrder = orderList; }
            BrowsePage.CHARACTER -> { characterIds = idList; characterOrder = orderList; }
            BrowsePage.STAFF -> { staffIds = idList; staffOrder = orderList; }
            BrowsePage.STUDIO -> { studioIds = idList; studioOrder = orderList; }
        }

        userRepository.reorderFavorites(
            animeIds, mangaIds, characterIds, staffIds, studioIds, animeOrder, mangaOrder, characterOrder, staffOrder, studioOrder
        )
    }
}