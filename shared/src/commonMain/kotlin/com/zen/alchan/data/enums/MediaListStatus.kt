package com.zen.alchan.data.enums

enum class MediaListStatus {
    CURRENT,
    PLANNING,
    COMPLETED,
    DROPPED,
    PAUSED,
    REPEATING
}

fun getMediaListStatusEnum(mediaListStatus: String?): MediaListStatus? {
    return MediaListStatus.entries.firstOrNull { it.name.equals(mediaListStatus, true) }
}
