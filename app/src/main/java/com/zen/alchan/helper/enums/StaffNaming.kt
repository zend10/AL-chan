package com.zen.alchan.helper.enums

import com.zen.alchan.helper.extensions.convertFromSnakeCase

enum class StaffNaming : Naming {
    FOLLOW_ANILIST,
    FIRST_MIDDLE_LAST,
    LAST_MIDDLE_FIRST,
    NATIVE
}

fun StaffNaming.getString(): String {
    return name.convertFromSnakeCase()
}