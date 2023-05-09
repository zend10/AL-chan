package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.MediaEdge
import com.zen.alchan.data.response.anilist.Staff

data class StaffItem(
    val staff: Staff = Staff(),
    val media: List<MediaEdge> = listOf(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_BIO = 100
        const val VIEW_TYPE_CHARACTER = 200
        const val VIEW_TYPE_MEDIA = 300
    }
}