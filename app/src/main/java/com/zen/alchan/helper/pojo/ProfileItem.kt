package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.User


data class ProfileItem(
    val bio: String? = null,
    val affinity: Pair<Double?, Double?>? = null,
    val tendency: Pair<Tendency?, Tendency?>? = null,
    val favoriteCharacters: List<Character>? = null,
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_BIO = 100
        const val VIEW_TYPE_AFFINITY = 200
        const val VIEW_TYPE_TENDENCY = 300
        const val VIEW_TYPE_FAVORITE_ANIME = 400
        const val VIEW_TYPE_FAVORITE_MANGA = 401
        const val VIEW_TYPE_FAVORITE_CHARACTER = 402
        const val VIEW_TYPE_FAVORITE_STAFF = 403
        const val VIEW_TYPE_FAVORITE_STUDIO = 404
        const val VIEw_TYPE_STATS = 500
        const val VIEW_TYPE_REVIEW = 600
    }
}