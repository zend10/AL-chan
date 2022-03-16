package com.zen.alchan.data.response.anilist

import com.zen.alchan.helper.extensions.convertFromSnakeCase
import type.CharacterRole
import type.MediaRelation

data class MediaEdge(
    val node: Media = Media(),
    val relationType: MediaRelation? = null,
    val characterRole: CharacterRole? = null,
    val staffRole: String = ""
) {
    fun getRelationTypeString(): String {
        return relationType?.name?.convertFromSnakeCase(true) ?: ""
    }
}