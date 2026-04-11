package com.zen.alchan.data.enums

enum class MediaStatus {
    CANCELLED,
    FINISHED,
    HIATUS,
    NOT_YET_RELEASED,
    RELEASING
}

fun getMediaStatusEnum(mediaStatus: String?): MediaStatus? {
    return MediaStatus.entries.firstOrNull { it.name.equals(mediaStatus, true) }
}