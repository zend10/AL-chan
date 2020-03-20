package com.zen.alchan.data.response

import type.MediaFormat
import type.MediaStatus

class Media(
    val id: Int,
    val title: MediaTitle?,
    val format: MediaFormat?,
    val status: MediaStatus?,
    val episodes: Int?,
    val coverImage: MediaCoverImage?,
    var isFavourite: Boolean,
    val isAdult: Boolean?,
    var nextAiringEpisode: AiringSchedule?,
    val siteUrl: String?
)