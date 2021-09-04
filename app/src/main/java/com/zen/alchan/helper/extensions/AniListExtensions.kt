package com.zen.alchan.helper.extensions

import android.content.Context
import com.apollographql.apollo.api.EnumValue
import com.zen.alchan.R
import type.*

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

inline fun <reified T: Enum<*>> getNonUnknownValues(): List<T> {
    return enumValues<T>().filter { it.name != "UNKNOWN__" }
}