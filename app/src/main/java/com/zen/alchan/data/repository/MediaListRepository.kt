package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource
import com.zen.alchan.data.response.MediaListCollection

interface MediaListRepository {
    val animeListDataResponse: LiveData<Resource<Boolean>>
    val animeListData: LiveData<MediaListCollection>

    fun getAnimeListData()
    fun retrieveAnimeListData()
}