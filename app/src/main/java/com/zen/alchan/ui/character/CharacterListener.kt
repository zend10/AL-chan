package com.zen.alchan.ui.character

import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Staff

interface CharacterListener {
    fun navigateToStaff(staff: Staff)
    fun showStaffMedia(staff: Staff)
    fun navigateToCharacterMedia()

    val characterMediaListener: CharacterMediaListener

    interface CharacterMediaListener {
        fun navigateToMedia(media: Media)
    }
}