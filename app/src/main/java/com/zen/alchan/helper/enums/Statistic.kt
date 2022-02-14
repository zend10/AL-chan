package com.zen.alchan.helper.enums

import com.zen.alchan.R

enum class Statistic {
    STATUS,
    FORMAT,
    SCORE,
    LENGTH,
    RELEASE_YEAR,
    START_YEAR,
    GENRE,
    TAG,
    COUNTRY,
    VOICE_ACTOR,
    STAFF,
    STUDIO
}

fun Statistic.getStringResource(): Int {
    return when (this) {
        Statistic.STATUS -> R.string.status
        Statistic.FORMAT -> R.string.format
        Statistic.SCORE -> R.string.score
        Statistic.LENGTH -> R.string.length
        Statistic.RELEASE_YEAR -> R.string.release_year
        Statistic.START_YEAR -> R.string.start_year
        Statistic.GENRE -> R.string.genre
        Statistic.TAG -> R.string.tag
        Statistic.COUNTRY -> R.string.country
        Statistic.VOICE_ACTOR -> R.string.voice_actor
        Statistic.STAFF -> R.string.staff
        Statistic.STUDIO -> R.string.studio
    }
}