package com.zen.alchan.helper.extensions

import java.util.*

fun String.replaceUnderscore(): String {
    return replace("_", " ")
}

fun String.convertFromSnakeCase(toUpper: Boolean = false): String {
    val splitText = this.split("_")
    val jointText = splitText.joinToString(" ") { it.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault()) }
    return if (toUpper) jointText.toUpperCase(Locale.getDefault()) else jointText
}