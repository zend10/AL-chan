package com.zen.alchan.helper.pojo

import type.CharacterRole
import type.StaffLanguage

class MediaCharacters(
    val characterId: Int?,
    val characterName: String?,
    val characterImage: String?,
    val role: CharacterRole?,
    val voiceActorId: Int?,
    val voiceActorName: String?,
    val voiceActorLanguage: StaffLanguage?,
    val voiceActorImage: String?,
    val isViewMore: Boolean = false
)