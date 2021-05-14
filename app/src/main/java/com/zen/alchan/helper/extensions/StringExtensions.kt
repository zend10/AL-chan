package com.zen.alchan.helper.extensions

import java.util.*

fun String.replaceUnderscore(): String {
    return replace("_", " ")
}

fun String.convertFromSnakeCase(): String {
    val splitText = this.split("_")
    return splitText.joinToString(" ") { it.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault()) }
}