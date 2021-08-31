package com.zen.alchan.data.converter

import com.zen.alchan.data.response.anilist.MediaTag

fun TagQuery.Data.convert(): List<MediaTag> {
    return mediaTagCollection?.mapNotNull {
        MediaTag(
            id = it?.id ?: 0,
            name = it?.name ?: "",
            description = it?.description ?: "",
            category = it?.category ?: "",
            rank = it?.rank ?: 0,
            isGeneralSpoiler = it?.isGeneralSpoiler ?: false,
            isMediaSpoiler = it?.isMediaSpoiler ?: false,
            isAdult = it?.isAdult ?: false
        )
    } ?: listOf()
}