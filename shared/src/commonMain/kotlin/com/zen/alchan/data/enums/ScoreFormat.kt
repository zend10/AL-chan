package com.zen.alchan.data.enums

enum class ScoreFormat {
    POINT_100,
    POINT_10_DECIMAL,
    POINT_10,
    POINT_5,
    POINT_3
}

fun getScoreFormatEnum(scoreFormat: String?): ScoreFormat? {
    return ScoreFormat.entries.firstOrNull { it.name.equals(scoreFormat, true) }
}
