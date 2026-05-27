package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.Studio
import kotlinx.serialization.Serializable

@Serializable
data class StudioResponse(
    val id: Int? = null,
    val name: String? = null
) {
    fun toModel(isMain: Boolean = false): Studio {
        return Studio(
            id = id?.toString() ?: "",
            name = name ?: "",
            isMain = isMain
        )
    }
}
