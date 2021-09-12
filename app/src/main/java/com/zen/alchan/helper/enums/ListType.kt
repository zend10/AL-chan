package com.zen.alchan.helper.enums

import com.zen.alchan.helper.extensions.convertFromSnakeCase

enum class ListType {
    LINEAR,
    GRID,
    SIMPLIFIED,
    ALBUM
}

fun ListType.getString(): String {
    return name.convertFromSnakeCase()
}