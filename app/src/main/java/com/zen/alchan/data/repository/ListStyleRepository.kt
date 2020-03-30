package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.pojo.ListStyle

interface ListStyleRepository {
    val animeListStyle: ListStyle
    val mangaListStyle: ListStyle

    val animeListStyleLiveData: LiveData<ListStyle>

    fun saveAnimeListStyle(newAnimeListStyle: ListStyle)
}