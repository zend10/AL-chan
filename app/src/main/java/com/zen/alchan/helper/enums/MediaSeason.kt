package com.zen.alchan.helper.enums

import com.zen.alchan.helper.extensions.convertFromSnakeCase

enum class MediaSeason {
    WINTER,
    SPRING,
    SUMMER,
    FALL
}

fun MediaSeason.getSeasonName(): String {
    return this.name.convertFromSnakeCase(true)
}

fun MediaSeason.getAniListMediaSeason(): type.MediaSeason {
    return when (this) {
        MediaSeason.WINTER -> type.MediaSeason.WINTER
        MediaSeason.SPRING -> type.MediaSeason.SPRING
        MediaSeason.SUMMER -> type.MediaSeason.SUMMER
        MediaSeason.FALL -> type.MediaSeason.FALL
    }
}