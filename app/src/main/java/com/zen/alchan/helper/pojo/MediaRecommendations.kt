package com.zen.alchan.helper.pojo

import type.MediaFormat

class MediaRecommendations(
    val mediaId: Int,
    val rating: Int?,
    val title: String?,
    val format: MediaFormat?,
    val averageScore: Int?,
    val favourites: Int?,
    val coverImage: String?
)