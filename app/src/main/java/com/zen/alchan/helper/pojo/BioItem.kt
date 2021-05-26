package com.zen.alchan.helper.pojo

data class BioItem(
    val animeAffinity: Double? = null,
    val mangaAffinity: Double? = null,
    val bioText: String = "",
    val animeTendency: Tendency? = null,
    val mangaTendency: Tendency? = null,
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_AFFINITY = 100
        const val VIEW_TYPE_ABOUT = 200
        const val VIEW_TYPE_TENDENCY = 300
    }
}