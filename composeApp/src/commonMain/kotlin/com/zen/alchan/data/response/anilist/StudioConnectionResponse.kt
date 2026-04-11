package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class StudioConnectionResponse(
    val edges: List<StudioEdgeResponse>? = null
)
