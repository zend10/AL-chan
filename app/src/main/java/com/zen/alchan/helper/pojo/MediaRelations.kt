package com.zen.alchan.helper.pojo

import type.MediaFormat
import type.MediaRelation
import type.MediaType

class MediaRelations(
    val mediaId: Int,
    val title: String,
    val coverImage: String?,
    val mediaType: MediaType,
    val format: MediaFormat,
    val relationType: MediaRelation
)