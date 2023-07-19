package com.zen.alchan.helper.utils

import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.type.MediaSeason
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    fun getCurrentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun getCurrentSeason(): MediaSeason {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        return when (currentMonth) {
            Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> MediaSeason.WINTER
            in Calendar.MARCH..Calendar.MAY -> MediaSeason.SPRING
            in Calendar.JUNE..Calendar.AUGUST -> MediaSeason.SUMMER
            in Calendar.SEPTEMBER..Calendar.NOVEMBER -> MediaSeason.FALL
            else -> MediaSeason.WINTER
        }
    }

    fun getCurrentFuzzyDate(): FuzzyDate {
        val calendar = Calendar.getInstance()
        return FuzzyDate(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1,
            day = calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun getMillisFromFuzzyDate(fuzzyDate: FuzzyDate?): Long {
        if (fuzzyDate?.year == null || fuzzyDate.month == null || fuzzyDate.day == null)
            return 0

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, fuzzyDate.year)
        calendar.set(Calendar.MONTH, fuzzyDate.month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, fuzzyDate.day)
        return calendar.timeInMillis
    }

    fun getReadableDateFromFuzzyDate(fuzzyDate: FuzzyDate?): String {
        if (fuzzyDate?.year == null || fuzzyDate.month == null || fuzzyDate.day == null)
            return "-"

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, fuzzyDate.year)
        calendar.set(Calendar.MONTH, fuzzyDate.month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, fuzzyDate.day)

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun displayInDayDateTimeFormat(seconds: Int): String {
        val dateFormat = SimpleDateFormat("E, dd MMM yyyy, hh:mm a", Locale.getDefault())
        val date = Date(seconds * 1000L)
        return dateFormat.format(date)
    }

    fun displayInDateFormat(seconds: Int): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = Date(seconds * 1000L)
        return dateFormat.format(date)
    }

    fun displayInTimeFormat(seconds: Int): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date (seconds * 1000L)
        return dateFormat.format(date)
    }

    fun getDaysAndHours(minutes: Int): Pair<Int, Int> {
        val days = minutes / 60 / 24
        val hours = (minutes - (days * 60 * 24)) / 60
        return days to hours
    }

    fun getMinutesRemainingUntilTheNextHour(): Int {
        val calendar = Calendar.getInstance()
        val minutes = calendar.get(Calendar.MINUTE)
        return 60 - minutes
    }

    fun getTodayInSeconds(): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return (calendar.timeInMillis / 1000).toInt()
    }

    fun isBetweenTwoDates(fromDateString: String, untilDateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
        return try {
            val fromDate = dateFormat.parse(fromDateString)?.time ?: 0
            val untilDate = dateFormat.parse(untilDateString)?.time ?: 0
            val today = getCurrentTimeInMillis()
            today in fromDate..untilDate
        } catch (e: Exception) {
            false
        }
    }
}