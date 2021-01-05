package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.FuzzyDate
import type.*

class CharacterMedia(
    val mediaId: Int?,
    val mediaTitle: String?,
    val mediaImage: String?,
    val mediaType: MediaType?,
    val mediaFormat: MediaFormat?,
    val mediaStartDate: Int?,
    val mediaAverageScore: Int?,
    val mediaPopularity: Int?,
    val mediaFavourites: Int?,
    val mediaListStatus: MediaListStatus?,
    val role: CharacterRole?
)

class CharacterVoiceActors(
    val voiceActorId: Int?,
    val voiceActorName: String?,
    val voiceActorImage: String?,
    val voiceActorLanguage: StaffLanguage?,
    val characterMediaList: ArrayList<CharacterMedia>?
)