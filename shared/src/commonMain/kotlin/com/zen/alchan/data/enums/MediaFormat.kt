package com.zen.alchan.data.enums

enum class MediaFormat {
    MANGA,
    MOVIE,
    MUSIC,
    NOVEL,
    ONA,
    ONE_SHOT,
    OVA,
    SPECIAL,
    TV,
    TV_SHORT
}

fun getMediaFormatEnum(mediaFormat: String?): MediaFormat? {
    return MediaFormat.entries.firstOrNull { it.name.equals(mediaFormat, true) }
}