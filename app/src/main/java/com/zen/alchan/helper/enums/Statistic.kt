package com.zen.alchan.helper.enums

enum class Statistic(val showChart: Boolean) {
    STATUS(true),
    FORMAT(true),
    SCORE(true),
    LENGTH(true),
    RELEASE_YEAR(true),
    START_YEAR(true),
    GENRE(false),
    TAG(false),
    COUNTRY(true),
    VOICE_ACTOR(false),
    STAFF(false),
    STUDIO(false)
}