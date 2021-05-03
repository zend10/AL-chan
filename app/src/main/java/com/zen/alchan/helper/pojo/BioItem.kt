package com.zen.alchan.helper.pojo

data class BioItem(
    val animeAffinity: Double? = null,
    val mangaAffinity: Double? = null,
    val bioText: String = "",
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_AFFINITY = 101
        const val VIEW_TYPE_ABOUT = 201
    }
}