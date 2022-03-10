package com.zen.alchan.data.response.anilist

import type.CharacterRole
import type.MediaRelation

data class MediaEdge(
    val node: Media = Media(),
    val relationType: MediaRelation? = null,
    val characterRole: CharacterRole? = null,
    val staffRole: String = ""
)