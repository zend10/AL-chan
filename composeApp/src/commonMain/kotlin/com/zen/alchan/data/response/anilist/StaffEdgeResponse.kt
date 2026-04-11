package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class StaffEdgeResponse(
    val node: StaffResponse? = null,
    val role: String? = null
)
