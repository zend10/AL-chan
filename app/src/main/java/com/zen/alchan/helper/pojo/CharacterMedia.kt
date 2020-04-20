package com.zen.alchan.helper.pojo

import type.CharacterRole
import type.MediaType
import type.StaffLanguage

class CharacterMedia(
    val mediaId: Int?,
    val mediaTitle: String?,
    val mediaImage: String?,
    val mediaType: MediaType?,
    val role: CharacterRole?,
    val voiceActorList: List<CharacterVoiceActors>?
)

class CharacterVoiceActors(
    val voiceActorId: Int?,
    val voiceActorName: String?,
    val voiceActorImage: String?,
    val voiceActorLanguage: StaffLanguage?
)