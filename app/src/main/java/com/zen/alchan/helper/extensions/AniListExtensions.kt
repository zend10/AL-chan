package com.zen.alchan.helper.extensions

import android.content.Context
import android.graphics.Color
import com.zen.R
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.type.*

fun ScoreFormat.getString(context: Context): String {
    return context.getString(getStringResource())
}

fun ScoreFormat.getStringResource(): Int {
    return when (this) {
        ScoreFormat.POINT_100 -> R.string.hundred_point
        ScoreFormat.POINT_10_DECIMAL -> R.string.ten_point_decimal
        ScoreFormat.POINT_10 -> R.string.ten_point
        ScoreFormat.POINT_5 -> R.string.five_star
        ScoreFormat.POINT_3 -> R.string.three_point_smiley
        else -> R.string.hundred_point
    }
}

fun com.zen.alchan.type.MediaType.getMediaType(): MediaType {
    return when (this) {
        com.zen.alchan.type.MediaType.ANIME -> MediaType.ANIME
        com.zen.alchan.type.MediaType.MANGA -> MediaType.MANGA
        else -> MediaType.ANIME
    }
}

fun MediaFormat.getString(): String {
    return name.convertFromSnakeCase(true)
}

fun MediaSeason.getString(): String {
    return name.convertFromSnakeCase(true)
}

fun MediaSource.getString(): String {
    return name.convertFromSnakeCase(true)
}

fun MediaStatus.getString(): String {
    return name.convertFromSnakeCase(true)
}

fun MediaListStatus.getString(mediaType: MediaType): String {
    return when (this) {
        MediaListStatus.CURRENT -> when (mediaType) {
            MediaType.ANIME -> "Watching"
            MediaType.MANGA -> "Reading"
        }
        MediaListStatus.REPEATING -> when (mediaType) {
            MediaType.ANIME -> "Rewatching"
            MediaType.MANGA -> "Rereading"
        }
        MediaListStatus.COMPLETED -> "Completed"
        MediaListStatus.PAUSED -> "Paused"
        MediaListStatus.DROPPED -> "Dropped"
        MediaListStatus.PLANNING -> "Planning"
        else -> this.name.convertFromSnakeCase()
    }
}

fun MediaListStatus.getColor(): String {
    return when (this) {
        MediaListStatus.CURRENT -> "#68D639"
        MediaListStatus.PLANNING -> "#02A9FF"
        MediaListStatus.COMPLETED, MediaListStatus.REPEATING -> "#9256F3"
        MediaListStatus.DROPPED -> "#F779A4"
        MediaListStatus.PAUSED -> "#E85D75"
        else -> "#68D639"
    }
}

fun UserStatisticsSort.getStringResource(mediaType: MediaType): Int {
    return when (this) {
        UserStatisticsSort.COUNT_DESC -> R.string.title_count
        UserStatisticsSort.PROGRESS_DESC -> when (mediaType) {
            MediaType.ANIME -> R.string.time_watched
            MediaType.MANGA -> R.string.chapters_read
        }
        UserStatisticsSort.MEAN_SCORE_DESC -> R.string.mean_score
        else -> R.string.title_count
    }
}

fun StaffLanguage.getString(): String {
    return name.convertFromSnakeCase(true)
}

fun MediaSort.getStringResource(): Int {
    return when (this) {
        MediaSort.POPULARITY_DESC -> R.string.popularity
        MediaSort.START_DATE_DESC -> R.string.latest_release
        MediaSort.START_DATE -> R.string.earliest_release
        MediaSort.FAVOURITES_DESC -> R.string.favorites
        MediaSort.SCORE_DESC -> R.string.score
        MediaSort.TITLE_ROMAJI -> R.string.title_romaji
        MediaSort.TITLE_ENGLISH -> R.string.title_english
        MediaSort.TITLE_NATIVE -> R.string.title_native
        else -> 0
    }
}

fun ActivityType.getString(): String {
    return name.convertFromSnakeCase(false)
}

fun Int.getScoreColor(): Int? {
    val hexColor = when (this) {
        10 -> "#d2492d"
        20 -> "#d2642c"
        30 -> "#d2802e"
        40 -> "#d29d2f"
        50 -> "#d2b72e"
        60 -> "#d3d22e"
        70 -> "#b8d22c"
        80 -> "#9cd42e"
        90 -> "#81d12d"
        100 -> "#63d42e"
        else -> null
    }
    return hexColor?.let {
        Color.parseColor(hexColor)
    }
}

inline fun <reified T: Enum<T>> getNonUnknownValues(): List<T> {
    return enumValues<T>().filter { it.name != "UNKNOWN__" }
}