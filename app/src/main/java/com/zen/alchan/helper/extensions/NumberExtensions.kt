package com.zen.alchan.helper.extensions

import android.content.Context
import androidx.annotation.PluralsRes
import com.zen.alchan.helper.utils.TimeUtil
import java.text.NumberFormat

fun Int.getNumberFormatting(): String {
    return NumberFormat.getIntegerInstance().format(this)
}

fun Long.moreThanADay(): Boolean {
    return TimeUtil.getCurrentTimeInMillis() > this + 24 * 60 * 60 * 1000
}

fun Double.formatTwoDecimal(): String {
    return String.format("%.2f", this)
}

fun Int.showUnit(context: Context, @PluralsRes pluralResId: Int): String {
    return "$this ${context.resources.getQuantityString(pluralResId, this)}"
}