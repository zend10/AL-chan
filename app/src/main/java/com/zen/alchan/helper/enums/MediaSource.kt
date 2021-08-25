package com.zen.alchan.helper.enums

import com.zen.alchan.helper.extensions.convertFromSnakeCase


enum class MediaSource {
    ORIGINAL,
    MANGA,
    LIGHT_NOVEL,
    VISUAL_NOVEL,
    VIDEO_GAME,
    OTHER,
    NOVEL,
    DOUJINSHI,
    ANIME
}

fun MediaSource.getSourceName(): String {
    return this.name.convertFromSnakeCase(true)
}

fun MediaSource.getAniListMediaSource(): type.MediaSource {
    return when (this) {
        MediaSource.ORIGINAL -> type.MediaSource.ORIGINAL
        MediaSource.MANGA -> type.MediaSource.MANGA
        MediaSource.LIGHT_NOVEL -> type.MediaSource.LIGHT_NOVEL
        MediaSource.VISUAL_NOVEL -> type.MediaSource.VISUAL_NOVEL
        MediaSource.VIDEO_GAME -> type.MediaSource.VIDEO_GAME
        MediaSource.OTHER -> type.MediaSource.OTHER
        MediaSource.NOVEL -> type.MediaSource.NOVEL
        MediaSource.DOUJINSHI -> type.MediaSource.DOUJINSHI
        MediaSource.ANIME -> type.MediaSource.ANIME
    }
}