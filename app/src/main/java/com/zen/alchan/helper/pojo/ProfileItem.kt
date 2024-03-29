package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.*


data class ProfileItem(
    val bio: String? = null,
    var affinity: Pair<Affinity?, Affinity?>? = null,
    val tendency: Pair<Tendency?, Tendency?>? = null,
    val favoriteMedia: List<Media>? = null,
    val favoriteCharacters: List<Character>? = null,
    val favoriteStaff: List<Staff>? = null,
    val favoriteStudios: List<Studio>? = null,
    val animeStats: UserStatistics? = null,
    val mangaStats: UserStatistics? = null,
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