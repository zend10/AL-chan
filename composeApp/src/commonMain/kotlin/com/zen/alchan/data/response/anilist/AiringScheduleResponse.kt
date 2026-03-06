package com.zen.alchan.data.response.anilist

import kotlinx.serialization.Serializable

@Serializable
data class AiringScheduleResponse(
    val episode: Int? = null
)
