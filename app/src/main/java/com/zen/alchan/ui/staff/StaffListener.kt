package com.zen.alchan.ui.staff

import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Media

interface StaffListener {

    fun navigateToStaffCharacter()
    fun navigateToStaffMedia()

    val staffCharacterListener: StaffCharacterListener
    val staffMediaListener: StaffMediaListener

    interface StaffCharacterListener {
        fun navigateToCharacter(character: Character)
    }

    interface StaffMediaListener {
        fun navigateToMedia(media: Media)
    }
}