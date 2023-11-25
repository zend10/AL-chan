package com.zen.alchan.helper.enums

import android.content.Context
import com.zen.R
import com.zen.alchan.helper.extensions.showUnit

enum class ActivityMergeTime(val minute: Int) {
    NEVER(0),
    THIRTY_MINUTES(30),
    ONE_HOUR(60),
    TWO_HOURS(60 * 2),
    THREE_HOURS(60 * 3),
    SIX_HOURS(60 * 6),
    TWELVE_HOURS(60 * 12),
    ONE_DAY(60 * 24),
    TWO_DAYS(60 * 24 * 2),
    THREE_DAYS(60 * 24 * 3),
    ONE_WEEK(60 * 24 * 7),
    TWO_WEEKS(60 * 24 * 14),
    ALWAYS(60 * 24 * 14 + 1)
}

fun ActivityMergeTime.getString(context: Context): String {
    return when (this) {
        ActivityMergeTime.NEVER -> context.getString(R.string.never)
        ActivityMergeTime.THIRTY_MINUTES -> minute.showUnit(context, R.plurals.minute)
        ActivityMergeTime.ONE_HOUR,
        ActivityMergeTime.TWO_HOURS,
        ActivityMergeTime.THREE_HOURS,
        ActivityMergeTime.SIX_HOURS,
        ActivityMergeTime.TWELVE_HOURS -> (minute / 60).showUnit(context, R.plurals.hour)
        ActivityMergeTime.ONE_DAY,
        ActivityMergeTime.TWO_DAYS,
        ActivityMergeTime.THREE_DAYS -> (minute / 60 / 24).showUnit(context, R.plurals.day)
        ActivityMergeTime.ONE_WEEK,
        ActivityMergeTime.TWO_WEEKS -> (minute / 60 / 24 / 7).showUnit(context, R.plurals.week)
        ActivityMergeTime.ALWAYS -> context.getString(R.string.always)
    }
}