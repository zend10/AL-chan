package com.zen.alchan.ui.staff.character

import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media

interface StaffCharacterListListener {
    fun navigateToCharacter(character: Character)
    fun navigateToMedia(media: Media)
}