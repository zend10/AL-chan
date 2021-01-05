package com.zen.alchan.ui.browse.character

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.pojo.CharacterMedia
import com.zen.alchan.helper.pojo.CharacterVoiceActors
import type.MediaFormat
import type.MediaSort
import type.StaffLanguage

class CharacterViewModel(private val browseRepository: BrowseRepository,
                         private val userRepository: UserRepository,
                         private val gson: Gson
) : ViewModel() {

    var characterId: Int? = null
    var currentCharacterData: CharacterQuery.Character? = null

    var page = 1
    var hasNextPage = true

    var isInit = false

    var characterMedia = ArrayList<CharacterMedia>()
    var characterVoiceActors = ArrayList<CharacterVoiceActors>()

    // for media filter
    var sortBy: MediaSort? = null
    var orderByDescending: Boolean = true
    var selectedFormats: ArrayList<MediaFormat>? = null
    var showOnlyOnList: Boolean? = null

    val characterData by lazy {
        browseRepository.characterData
    }

    val characterMediaData by lazy {
        browseRepository.characterMediaData
    }

    val characterIsFavoriteData by lazy {
        browseRepository.characterIsFavoriteData
    }

    val toggleFavouriteResponse by lazy {
        userRepository.toggleFavouriteResponse
    }

    fun getCharacter() {
        if (characterId != null) browseRepository.getCharacter(characterId!!)
    }

    fun getCharacterMedia() {
        if (hasNextPage && characterId != null) browseRepository.getCharacterMedia(characterId!!, page)
    }

    fun checkCharacterIsFavorite() {
        if (characterId != null) browseRepository.checkCharacterIsFavorite(characterId!!)
    }

    fun updateFavorite() {
        if (characterId != null) {
            userRepository.toggleFavourite(null, null, characterId, null, null)
        }
    }

    fun getFilteredMedia(): ArrayList<CharacterMedia> {
        val filteredList = ArrayList<CharacterMedia>()
        characterMedia.forEach {
            if (showOnlyOnList == true && it.mediaListStatus == null) {
                return@forEach
            }

            if (showOnlyOnList == false && it.mediaListStatus != null) {
                return@forEach
            }

            if (!selectedFormats.isNullOrEmpty() && selectedFormats?.contains(it.mediaFormat) == false) {
                return@forEach
            }

            filteredList.add(it)
        }

        when (sortBy) {
            MediaSort.POPULARITY -> {
                if (orderByDescending) {
                    filteredList.sortByDescending { it.mediaPopularity ?: 0 }
                } else {
                    filteredList.sortBy { it.mediaPopularity ?: 0 }
                }
            }
            MediaSort.SCORE -> {
                if (orderByDescending) {
                    filteredList.sortByDescending { it.mediaAverageScore ?: 0 }
                } else {
                    filteredList.sortBy { it.mediaAverageScore ?: 0 }
                }
            }
            MediaSort.FAVOURITES -> {
                if (orderByDescending) {
                    filteredList.sortByDescending { it.mediaFavourites ?: 0 }
                } else {
                    filteredList.sortBy { it.mediaFavourites ?: 0 }
                }
            }
            MediaSort.TITLE_ROMAJI -> {
                if (orderByDescending) {
                    filteredList.sortByDescending { it.mediaTitle ?: "" }
                } else {
                    filteredList.sortBy { it.mediaTitle ?: "" }
                }
            }
            MediaSort.START_DATE -> {
                if (orderByDescending) {
                    filteredList.sortByDescending { it.mediaStartDate ?: 0 }
                } else {
                    filteredList.sortBy { it.mediaStartDate ?: 0 }
                }
            }
            else -> {
                if (orderByDescending) {
                    filteredList.sortByDescending { it.mediaPopularity ?: 0 }
                } else {
                    filteredList.sortBy { it.mediaPopularity ?: 0 }
                }
            }
        }

        return filteredList
    }

    fun getSerializedSelectedFormats(): String? {
        return if (!selectedFormats.isNullOrEmpty()) {
            gson.toJson(selectedFormats)
        } else {
            null
        }
    }
}