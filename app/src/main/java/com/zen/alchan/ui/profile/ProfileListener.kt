package com.zen.alchan.ui.profile

import com.zen.alchan.data.response.anilist.Character

interface ProfileListener {

    interface FavoriteCharacterListener {
        fun navigateToCharacterFavorite()
        fun navigateToCharacter(character: Character)
    }

    val favoriteCharacterListener: FavoriteCharacterListener
}