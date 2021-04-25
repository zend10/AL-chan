package com.zen.alchan.data.response

data class MediaListTypeOptions(
    val sectionOrder: List<String> = listOf(),
    val splitCompletedSectionByFormat: Boolean = false,
    val customLists: List<String> = listOf(),
    val advancedScoring: List<String> = listOf(),
    val advancedScoringEnabled: Boolean = false
)