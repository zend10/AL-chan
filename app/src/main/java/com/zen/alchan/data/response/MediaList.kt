package com.zen.alchan.data.response

import type.MediaListStatus

class MediaList(
    val id: Int,
    var status: MediaListStatus?,
    var score: Double?,
    var progress: Int?,
    var repeat: Int?,
    var priority: Int?,
    var private: Boolean?,
    var notes: String?,
    var hiddenFromStatusList: Boolean?,
    var customLists: Any?,
    var advancedScores: Any?,
    var startedAt: FuzzyDate?,
    var completedAt: FuzzyDate?,
    var updatedAt: Int?,
    var createdAt: Int?,
    var media: Media?
)