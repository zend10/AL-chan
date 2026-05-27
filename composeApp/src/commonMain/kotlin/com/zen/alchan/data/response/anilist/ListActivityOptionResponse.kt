package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.enums.getMediaListStatusEnum
import com.zen.alchan.data.model.api.ListActivityOption
import kotlinx.serialization.Serializable

@Serializable
data class ListActivityOptionResponse(
    val disabled: Boolean? = null,
    val type: String? = null
) {
    fun toModel(): ListActivityOption {
        return ListActivityOption(
            disabled = disabled ?: false,
            type = getMediaListStatusEnum(type)
        )
    }
}
