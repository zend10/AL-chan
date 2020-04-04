package com.zen.alchan.data.localstorage

import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.pojo.ListStyle

interface ListStyleManager {
    val animeListStyle: ListStyle
    val mangaListStyle: ListStyle

    fun saveAnimeListStyle(newAnimeListStyle: ListStyle)
    fun saveMangaListStyle(newMangaListStyle: ListStyle)
}