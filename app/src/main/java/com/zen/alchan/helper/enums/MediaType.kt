package com.zen.alchan.helper.enums

import com.zen.alchan.R

enum class MediaType {
    ANIME,
    MANGA
}

fun MediaType.getAniListMediaType(): type.MediaType {
    return when (this) {
        MediaType.ANIME -> type.MediaType.ANIME
        MediaType.MANGA -> type.MediaType.MANGA
    }
}

fun MediaType.getStringResource(): Int {
    return when (this) {
        MediaType.ANIME -> R.string.anime
        MediaType.MANGA -> R.string.manga
    }
}