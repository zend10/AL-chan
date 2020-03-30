package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zen.alchan.data.localstorage.ListStyleManager
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.pojo.ListStyle

class ListStyleRepositoryImpl(private val listStyleManager: ListStyleManager) : ListStyleRepository {

    override val animeListStyle: ListStyle
        get() = listStyleManager.animeListStyle

    override val mangaListStyle: ListStyle
        get() = listStyleManager.mangaListStyle

    private val _animeListStyleLiveData = MutableLiveData<ListStyle>()
    override val animeListStyleLiveData: LiveData<ListStyle>
        get() = _animeListStyleLiveData

    override fun saveAnimeListStyle(newAnimeListStyle: ListStyle) {
        listStyleManager.saveAnimeListStyle(newAnimeListStyle)
        _animeListStyleLiveData.postValue(newAnimeListStyle)
    }
}