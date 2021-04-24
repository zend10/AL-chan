package com.zen.alchan.helper.extensions

import com.zen.alchan.helper.utils.TimeUtil
import java.text.NumberFormat

fun Int.getNumberFormatting(): String {
    return NumberFormat.getIntegerInstance().format(this)
}

fun Long.moreThanADay(): Boolean {
    return TimeUtil.getCurrentTimeInMillis() > this + 24 * 60 * 60 * 1000
}