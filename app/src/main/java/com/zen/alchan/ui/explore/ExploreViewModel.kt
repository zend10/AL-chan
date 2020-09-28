package com.zen.alchan.ui.explore

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.SearchRepository
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.pojo.MediaFilterData
import com.zen.alchan.helper.pojo.SearchResult
import type.*

class ExploreViewModel(private val searchRepository: SearchRepository,
                       val gson: Gson) : ViewModel() {

    var selectedExplorePage: BrowsePage? = null

    var page = 1
    var hasNextPage = true
    var isInit = false
    var searchResultList = ArrayList<SearchResult?>()

    var filterData: MediaFilterData? = null

    var explorePageArray = arrayOf(
        BrowsePage.ANIME.name, BrowsePage.MANGA.name, BrowsePage.CHARACTER.name, BrowsePage.STAFF.name, BrowsePage.STUDIO.name
    )

    val searchAnimeResponse by lazy {
        searchRepository.searchAnimeResponse
    }

    val searchMangaResponse by lazy {
        searchRepository.searchMangaResponse
    }

    val searchCharactersResponse by lazy {
        searchRepository.searchCharactersResponse
    }

    val searchStaffsResponse by lazy {
        searchRepository.searchStaffsResponse
    }

    val searchStudiosResponse by lazy {
        searchRepository.searchStudiosResponse
    }

    fun search(query: String) {
        when (selectedExplorePage) {
            BrowsePage.ANIME -> {
                searchRepository.searchAnime(
                    page,
                    query,
                    filterData
                )
            }
            BrowsePage.MANGA -> {
                searchRepository.searchManga(
                    page,
                    query,
                    filterData
                )
            }
            BrowsePage.CHARACTER -> searchRepository.searchCharacters(page, query, if (query.isBlank()) listOf(CharacterSort.FAVOURITES_DESC) else null)
            BrowsePage.STAFF -> searchRepository.searchStaffs(page, query, if (query.isBlank()) listOf(StaffSort.FAVOURITES_DESC) else null)
            BrowsePage.STUDIO -> searchRepository.searchStudios(page, query, if (query.isBlank()) listOf(StudioSort.FAVOURITES_DESC) else null)
        }
    }
}