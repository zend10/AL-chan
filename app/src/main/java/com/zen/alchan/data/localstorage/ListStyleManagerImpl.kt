package com.zen.alchan.data.localstorage

import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.pojo.ListStyle

class ListStyleManagerImpl(private val localStorage: LocalStorage) : ListStyleManager {

    override val animeListStyle: ListStyle
        get() = localStorage.animeListStyle ?: ListStyle()

    override val mangaListStyle: ListStyle
        get() = localStorage.mangaListStyle ?: ListStyle()

    override fun saveAnimeListStyle(newAnimeListStyle: ListStyle) {
        localStorage.animeListStyle = newAnimeListStyle
    }
}