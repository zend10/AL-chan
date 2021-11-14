package com.zen.alchan.ui.media

import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media

interface MediaListener {

    interface MediaCharacterListener {
        fun navigateToMediaCharacter(media: Media)
        fun navigateToCharacter(character: Character)
    }

    val mediaCharacterListener: MediaCharacterListener
}