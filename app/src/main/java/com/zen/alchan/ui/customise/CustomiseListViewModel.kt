package com.zen.alchan.ui.common.customise

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.ListStyleRepository
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.pojo.ListStyle
import type.MediaType

class CustomiseListViewModel(private val listStyleRepository: ListStyleRepository) : ViewModel() {

    var isInit = false
    var isImageChanged = false

    var mediaType: MediaType? = null
    var selectedImageUri: Uri? = null
    var selectedListStyle = ListStyle()
    val listTypeList = arrayListOf(ListType.LINEAR, ListType.GRID, ListType.SIMPLIFIED)

    val animeListStyle: ListStyle
        get() = listStyleRepository.animeListStyle

    val mangaListStyle: ListStyle
        get() = listStyleRepository.mangaListStyle

    fun saveListSettings() {
        if (mediaType == MediaType.ANIME) {
            listStyleRepository.saveAnimeListStyle(selectedListStyle)
        } else if (mediaType == MediaType.MANGA) {
            listStyleRepository.saveMangaListStyle(selectedListStyle)
        }
    }
}