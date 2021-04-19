package com.zen.alchan.helper.extensions

import java.text.NumberFormat

fun Int.getNumberFormatting(): String {
    return NumberFormat.getIntegerInstance().format(this)
}