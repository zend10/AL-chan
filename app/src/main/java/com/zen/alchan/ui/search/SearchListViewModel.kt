package com.zen.alchan.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.repository.SearchRepository
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.pojo.SearchResult
import type.MediaSort

class SearchListViewModel(private val searchRepository: SearchRepository) : ViewModel() {

    lateinit var searchPage: BrowsePage

    var page = 1
    var hasNextPage = true

    var isInit = false
    var searchResultList = ArrayList<SearchResult?>()

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

    val searchUsersResponse by lazy {
        searchRepository.searchUsersResponse
    }

    fun getObserver(): LiveData<*>? {
        return when (searchPage) {
            BrowsePage.ANIME -> searchAnimeResponse
            BrowsePage.MANGA -> searchMangaResponse
            BrowsePage.CHARACTER -> searchCharactersResponse
            BrowsePage.STAFF -> searchStaffsResponse
            BrowsePage.STUDIO -> searchStudiosResponse
            BrowsePage.USER -> searchUsersResponse
            else -> null
        }
    }

    fun search(query: String) {
        when (searchPage) {
            BrowsePage.ANIME -> searchRepository.searchAnime(page, query)
            BrowsePage.MANGA -> searchRepository.searchManga(page, query)
            BrowsePage.CHARACTER -> searchRepository.searchCharacters(page, query)
            BrowsePage.STAFF -> searchRepository.searchStaffs(page, query)
            BrowsePage.STUDIO -> searchRepository.searchStudios(page, query)
            BrowsePage.USER -> searchRepository.searchUsers(page, query)
        }
    }
}