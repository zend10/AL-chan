package com.zen.alchan.ui.media

import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Studio

interface MediaListener {

    interface MediaGenreListener {
        fun navigateToExplore(genre: Genre)
    }

    interface MediaCharacterListener {
        fun navigateToMediaCharacter(media: Media)
        fun navigateToCharacter(character: Character)
    }

    interface MediaStudioListener {
        fun navigateToStudio(studio: Studio)
    }

    val mediaGenreListener: MediaGenreListener
    val mediaCharacterListener: MediaCharacterListener
    val mediaStudioListener: MediaStudioListener
}