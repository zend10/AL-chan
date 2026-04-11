package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class StaffConnectionResponse(
    val edges: List<StaffEdgeResponse>? = null
)
