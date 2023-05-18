package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.StaffRoleType

data class CharacterItem(
    val character: Character = Character(),
    val voiceActors: List<StaffRoleType> = listOf(),
    var showFullDescription: Boolean = false,
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_BIO = 100
        const val VIEW_TYPE_INFO = 200
        const val VIEW_TYPE_STAFF = 300
        const val VIEW_TYPE_MEDIA = 400
    }
}