package com.zen.alchan.data.enums

enum class MediaType {
    ANIME,
    MANGA
}

fun getMediaTypeEnum(mediaType: String?): MediaType? {
    return MediaType.entries.firstOrNull { it.name.equals(mediaType, true) }
}