package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.StaffName
import kotlinx.serialization.Serializable

@Serializable
data class StaffNameResponse(
    val full: String? = null
) {
    fun toModel(): StaffName {
        return StaffName(
            full = full ?: ""
        )
    }
}
