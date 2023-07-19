package com.zen.alchan.helper.enums

import com.zen.alchan.helper.extensions.convertFromSnakeCase

enum class MediaNaming : Naming {
    FOLLOW_ANILIST,
    ENGLISH,
    ROMAJI,
    NATIVE
}

fun MediaNaming.getString(): String {
    return name.convertFromSnakeCase()
}