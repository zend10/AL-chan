package com.zen.alchan.data.response

import type.MediaListStatus

class MediaList(
    val id: Int,
    var status: MediaListStatus?,
    var score: Double?,
    var progress: Int?,
    var priority: Int?,
    var private: Boolean?,
    var hiddenFromStatusList: Boolean?,
    var media: Media?
)