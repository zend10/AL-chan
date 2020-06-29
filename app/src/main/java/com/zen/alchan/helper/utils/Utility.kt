package com.zen.alchan.helper.utils

import com.zen.alchan.helper.Constant
import java.text.SimpleDateFormat
import java.util.*

object Utility {

    // Easily get current timestamp
    // Not testable because this function's value is changing every time
    fun getCurrentTimestamp(): Long {
        return Calendar.getInstance().timeInMillis
    }

    // Easily get current year
    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    // Check if time in parameter is more than one day
    fun timeDiffMoreThanOneDay(timeInMillis: Long?): Boolean {
        return Calendar.getInstance().timeInMillis - (timeInMillis ?: 0) > 24 * 60 * 60 * 1000
    }

    // Convert year, month, date in parameter to string date format
    fun convertToDateFormat(year: Int?, month: Int?, day: Int?) : String? {
        if (year == null || month == null || day == null) {
            return null
        }

        val dateFormat = SimpleDateFormat(Constant.DEFAULT_DATE_FORMAT, Locale.US)

        return try {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            dateFormat.format(calendar.time)
        } catch (e: Exception) {
            null
        }
    }

    // Check if today is between the 2 dates (in iso date format / yyyy-MM-dd) in parameter
    fun isBetweenTwoDates(fromDateString: String, untilDateString: String): Boolean {
        val dateFormat = SimpleDateFormat(Constant.ISO_DATE_TIME_FORMAT, Locale.US)

        return try {
            val fromDate  = dateFormat.parse(fromDateString.substring(0..9) + " 00:00:00").time
            val untilDate = dateFormat.parse(untilDateString.substring(0..9) + " 23:59:59").time
            val today = Calendar.getInstance().timeInMillis
            today in fromDate..untilDate
        } catch (e: Exception) {
            false
        }
    }
}