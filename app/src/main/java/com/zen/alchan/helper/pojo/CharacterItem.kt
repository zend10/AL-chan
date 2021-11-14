package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Character

data class CharacterItem(
    val character: Character = Character(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_BIO = 100
        const val VIEW_TYPE_STAFF = 200
    }
}