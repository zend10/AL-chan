package com.zen.alchan.data.response.anilist

data class MediaListTypeOptions(
    val sectionOrder: List<String> = listOf(),
    val splitCompletedSectionByFormat: Boolean = false,
    val customLists: List<String> = listOf(),
    val advancedScoring: List<String> = listOf(),
    var advancedScoringEnabled: Boolean = false
)