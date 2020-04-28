package com.zen.alchan.ui.browse.character

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.BrowseRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.pojo.CharacterMedia
import com.zen.alchan.helper.pojo.CharacterVoiceActors
import type.StaffLanguage

class CharacterViewModel(private val browseRepository: BrowseRepository,
                         private val userRepository: UserRepository
) : ViewModel() {

    var characterId: Int? = null
    var currentCharacterData: CharacterQuery.Character? = null

    var page = 1
    var hasNextPage = true

    var isInit = false

    var characterMedia = ArrayList<CharacterMedia>()
    var characterVoiceActors = ArrayList<CharacterVoiceActors>()

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
}