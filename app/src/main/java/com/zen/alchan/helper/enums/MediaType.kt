package com.zen.alchan.helper.enums

import com.zen.R

enum class MediaType {
    ANIME,
    MANGA
}

fun MediaType.getAniListMediaType(): com.zen.alchan.type.MediaType {
    return when (this) {
        MediaType.ANIME -> com.zen.alchan.type.MediaType.ANIME
        MediaType.MANGA -> com.zen.alchan.type.MediaType.MANGA
    }
}

fun MediaType.getStringResource(): Int {
    return when (this) {
        MediaType.ANIME -> R.string.anime
        MediaType.MANGA -> R.string.manga
    }
}