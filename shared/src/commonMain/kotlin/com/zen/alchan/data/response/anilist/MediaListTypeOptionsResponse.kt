package com.zen.alchan.data.response.anilist

import com.zen.alchan.data.model.api.MediaListTypeOptions
import kotlinx.serialization.Serializable

@Serializable
data class MediaListTypeOptionsResponse(
    val sectionOrder: List<String>? = null,
    val splitCompletedSectionByFormat: Boolean? = null,
    val customLists: List<String>? = null,
    val advancedScoring: List<String>? = null,
    val advancedScoringEnabled: Boolean? = null
) {
    fun toModel(): MediaListTypeOptions {
        return MediaListTypeOptions(
            sectionOrder = sectionOrder ?: listOf(),
            splitCompletedSectionByFormat = splitCompletedSectionByFormat ?: false,
            customLists = customLists ?: listOf(),
            advancedScoring = advancedScoring ?: listOf(),
            advancedScoringEnabled = advancedScoringEnabled ?: false
        )
    }
}
