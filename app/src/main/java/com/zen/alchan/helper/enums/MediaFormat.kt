package com.zen.alchan.helper.enums

import com.zen.alchan.helper.extensions.convertFromSnakeCase

enum class MediaFormat {
    TV,
    TV_SHORT,
    MOVIE,
    SPECIAL,
    OVA,
    ONA,
    MUSIC,
    MANGA,
    NOVEL,
    ONE_SHOT
}

fun MediaFormat.getFormatName(): String {
    return this.name.convertFromSnakeCase(true)
}

fun MediaFormat.getAniListMediaFormat(): type.MediaFormat {
    return when (this) {
        MediaFormat.TV -> type.MediaFormat.TV
        MediaFormat.TV_SHORT -> type.MediaFormat.TV_SHORT
        MediaFormat.MOVIE -> type.MediaFormat.MOVIE
        MediaFormat.SPECIAL -> type.MediaFormat.SPECIAL
        MediaFormat.OVA -> type.MediaFormat.OVA
        MediaFormat.ONA -> type.MediaFormat.ONA
        MediaFormat.MUSIC -> type.MediaFormat.MUSIC
        MediaFormat.MANGA -> type.MediaFormat.MANGA
        MediaFormat.NOVEL -> type.MediaFormat.NOVEL
        MediaFormat.ONE_SHOT -> type.MediaFormat.ONE_SHOT
    }
}