package com.zen.alchan.helper.utils

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import com.google.gson.reflect.TypeToken
import com.zen.alchan.R
import com.zen.alchan.data.response.FuzzyDate
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.toMillis
import type.ScoreFormat
import java.text.SimpleDateFormat
import java.util.*

object Utility {

    fun getCurrentTimestamp(): Long {
        return Calendar.getInstance().timeInMillis
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun timeDiffMoreThanOneDay(timeInMillis: Long?): Boolean {
        return Calendar.getInstance().timeInMillis - (timeInMillis ?: 0) > 24 * 60 * 60 * 1000
    }

    fun getScoreFormatString(scoreFormat: ScoreFormat?): String {
        return when (scoreFormat) {
            ScoreFormat.POINT_100 -> "100 Point (55/100)"
            ScoreFormat.POINT_10_DECIMAL -> "10 Point Decimal (5.5/10)"
            ScoreFormat.POINT_10 -> "10 Point (5/10)"
            ScoreFormat.POINT_5 -> "5 Star (3/5)"
            ScoreFormat.POINT_3 -> "3 Point Smiley :)"
            else -> ""
        }
    }

    fun getMediaListOrderByString(orderBy: String?): String {
        return when (orderBy) {
            "title" -> "Title"
            "score" -> "Score"
            "updatedAt" -> "Last Updated"
            "id" -> "Last Added"
            else -> ""
        }
    }

    fun convertToDateFormat(year: Int?, month: Int?, day: Int?) : String? {
        if (year == null || month == null || day == null) {
            return null
        }
        val dateFormat = SimpleDateFormat(Constant.DEFAULT_DATE_FORMAT, Locale.US)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        return dateFormat.format(calendar.time)
    }

    fun isBetweenTwoDates(fromDateString: String, untilDateString: String): Boolean {
        val dateFormat = SimpleDateFormat(Constant.ISO_DATE_FORMAT, Locale.US)

        return try {
            val fromDate  = dateFormat.parse(fromDateString).time
            val untilDate = dateFormat.parse(untilDateString).time
            val today = Calendar.getInstance().timeInMillis
            today in fromDate..untilDate
        } catch (e: Exception) {
            false
        }
    }
}