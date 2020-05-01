package com.zen.alchan.helper.pojo

import type.CharacterRole
import type.MediaFormat
import type.MediaType

class StaffCharacter(
    val characterId: Int?,
    val characterRole: CharacterRole?,
    val characterName: String?,
    val characterImage: String?,
    val characterMediaList: ArrayList<StaffCharacterMedia>?
)

class StaffCharacterMedia(
    val mediaId: Int?,
    val mediaTitle: String?,
    val mediaImage: String?,
    val mediaType: MediaType?,
    val mediaFormat: MediaFormat?
)