package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource

interface SearchRepository {
    val searchAnimeResponse: LiveData<Resource<SearchAnimeQuery.Data>>
    val searchMangaResponse: LiveData<Resource<SearchMangaQuery.Data>>
    val searchCharactersResponse: LiveData<Resource<SearchCharactersQuery.Data>>
    val searchStaffsResponse: LiveData<Resource<SearchStaffsQuery.Data>>
    val searchStudiosResponse: LiveData<Resource<SearchStudiosQuery.Data>>

    fun searchAnime(page: Int, search: String)
    fun searchManga(page: Int, search: String)
    fun searchCharacters(page: Int, search: String)
    fun searchStaffs(page: Int, search: String)
    fun searchStudios(page: Int, search: String)
}