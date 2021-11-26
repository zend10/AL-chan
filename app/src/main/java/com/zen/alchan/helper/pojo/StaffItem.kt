package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.Character
import com.zen.alchan.data.response.anilist.Staff

data class StaffItem(
    val staff: Staff = Staff(),
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_BIO = 100
    }
}