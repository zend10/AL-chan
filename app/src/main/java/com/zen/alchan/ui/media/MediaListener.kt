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

    interface MediaRelationsListener {
        fun navigateToMedia(media: Media)
    }

    val mediaGenreListener: MediaGenreListener
    val mediaCharacterListener: MediaCharacterListener
    val mediaStudioListener: MediaStudioListener
    val mediaRelationsListener: MediaRelationsListener
}