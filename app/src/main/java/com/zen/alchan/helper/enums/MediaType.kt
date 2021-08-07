package com.zen.alchan.helper.enums

enum class MediaType {
    ANIME,
    MANGA
}

fun MediaType?.getAniListMediaType(): type.MediaType {
    return when (this) {
        MediaType.ANIME -> type.MediaType.ANIME
        MediaType.MANGA -> type.MediaType.MANGA
        else -> type.MediaType.ANIME
    }
}