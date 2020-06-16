package com.zen.alchan.helper.pojo

import type.MediaFormat
import type.MediaType

class MediaRecommendations(
    val mediaId: Int,
    val rating: Int?,
    val title: String?,
    val format: MediaFormat?,
    val type: MediaType?,
    val averageScore: Int?,
    val favourites: Int?,
    val coverImage: String?
)