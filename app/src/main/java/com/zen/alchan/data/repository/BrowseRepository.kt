package com.zen.alchan.data.repository

import androidx.lifecycle.LiveData
import com.zen.alchan.data.network.Resource

interface BrowseRepository {
    val characterData: LiveData<Resource<CharacterQuery.Data>>
    val characterMediaData: LiveData<Resource<CharacterMediaConnectionQuery.Data>>
    val characterIsFavoriteData: LiveData<Resource<CharacterIsFavoriteQuery.Data>>

    fun getCharacter(id: Int)
    fun getCharacterMedia(id: Int, page: Int)
    fun checkCharacterIsFavorite(id: Int)
}