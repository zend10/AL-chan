package com.zen.alchan.helper.extensions

import java.util.*


fun String.convertFromSnakeCase(toUpper: Boolean = false): String {
    val splitText = this.split("_")
    val jointText = splitText.joinToString(" ") {
        it.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
    return if (toUpper) jointText.uppercase() else jointText
}