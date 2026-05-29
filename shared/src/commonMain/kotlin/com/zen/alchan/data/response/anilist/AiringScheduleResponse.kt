package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.AiringSchedule
import kotlinx.serialization.Serializable

@Serializable
data class AiringScheduleResponse(
    val episode: Int? = null
) {
    fun toModel(): AiringSchedule {
        return AiringSchedule(
            episode = episode ?: 0
        )
    }
}
