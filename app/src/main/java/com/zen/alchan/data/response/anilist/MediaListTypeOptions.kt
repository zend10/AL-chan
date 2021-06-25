package com.zen.alchan.data.response.anilist

data class MediaListTypeOptions(
    var sectionOrder: List<String> = listOf(),
    var splitCompletedSectionByFormat: Boolean = false,
    var customLists: List<String> = listOf(),
    var advancedScoring: List<String> = listOf(),
    var advancedScoringEnabled: Boolean = false
)