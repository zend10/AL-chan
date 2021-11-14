package com.zen.alchan.data.response.anilist

import type.CharacterRole

data class MediaEdge(
    val node: Media = Media(),
    val characterRole: CharacterRole? = null
)