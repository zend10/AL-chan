package com.zen.alchan.ui.media

import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Studio

interface MediaListener {

    interface MediaCharacterListener {
        fun navigateToMediaCharacter(media: Media)
        fun navigateToCharacter(character: Character)
    }

    interface MediaStudioListener {
        fun navigateToStudio(studio: Studio)
    }

    val mediaCharacterListener: MediaCharacterListener
    val mediaStudioListener: MediaStudioListener
}