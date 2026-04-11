package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class StudioEdgeResponse(
    val node: StudioResponse? = null,
    val isMain: Boolean? = null
)
