package com.zen.alchan.helper.extensions

import android.content.Context
import com.zen.alchan.R
import type.ScoreFormat

fun ScoreFormat.getString(context: Context): String {
    return when (this) {
        ScoreFormat.POINT_100 -> context.getString(R.string.hundred_point)
        ScoreFormat.POINT_10_DECIMAL -> context.getString(R.string.ten_point_decimal)
        ScoreFormat.POINT_10 -> context.getString(R.string.ten_point)
        ScoreFormat.POINT_5 -> context.getString(R.string.five_star)
        ScoreFormat.POINT_3 -> context.getString(R.string.three_point_smiley)
        else -> this.name.convertFromSnakeCase()
    }
}