package com.zen.alchan.helper.enums

import com.zen.alchan.helper.extensions.convertFromSnakeCase

enum class MediaStatus {
    FINISHED,
    RELEASING,
    NOT_YET_RELEASED,
    CANCELLED,
    HIATUS
}

fun MediaStatus.getStatusName(): String {
    return this.name.convertFromSnakeCase(true)
}

fun MediaStatus.getAniListMediaStatus(): type.MediaStatus {
    return when (this) {
        MediaStatus.FINISHED -> type.MediaStatus.FINISHED
        MediaStatus.RELEASING -> type.MediaStatus.RELEASING
        MediaStatus.NOT_YET_RELEASED -> type.MediaStatus.NOT_YET_RELEASED
        MediaStatus.CANCELLED -> type.MediaStatus.CANCELLED
        MediaStatus.HIATUS -> type.MediaStatus.HIATUS
    }
}