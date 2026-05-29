package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.Studio
import kotlinx.serialization.Serializable

@Serializable
data class StudioConnectionResponse(
    val edges: List<StudioEdgeResponse>? = null
) {
    fun toModel(): List<Studio> {
        return edges?.mapNotNull {
            it.node?.toModel(isMain = it.isMain ?: false)
        } ?: listOf()
    }
}

@Serializable
data class StudioEdgeResponse(
    val node: StudioResponse? = null,
    val isMain: Boolean? = null
)
