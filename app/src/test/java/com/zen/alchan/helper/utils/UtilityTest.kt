package com.zen.alchan.helper.utils

import com.zen.alchan.helper.Constant
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import type.MediaSeason
import java.text.SimpleDateFormat
import java.util.*

class UtilityTest {

    companion object {
        private const val CURRENT_YEAR = 2020
        private const val CURRENT_SEASON = "SUMMER"
    }

    @Test
    internal fun getCurrentYear_returnCorrectYear() {
        val currentYear = Utility.getCurrentYear()
        val expectedYear =
            CURRENT_YEAR

        assertEquals(expectedYear, currentYear)
        println("Year is generated correctly.")
    }

    @Test
    internal fun timeDiffMoreThanOneDay_returnTrue() {
        val twoDaysAgo = Calendar.getInstance().timeInMillis - 3600 * 24 * 10000
        val isMoreThanOneDay = Utility.timeDiffMoreThanOneDay(twoDaysAgo)

        assertEquals(true, isMoreThanOneDay)
        println("$twoDaysAgo is more than one day.")
    }

    @Test
    internal fun timeDiffMoreThanOneDay_returnFalse() {
        val oneHourAgo = Calendar.getInstance().timeInMillis - 3600 * 10000
        val isMoreThanOneDay = Utility.timeDiffMoreThanOneDay(oneHourAgo)

        assertEquals(false, isMoreThanOneDay)
        println("$oneHourAgo is not more than one day.")
    }

    @Test
    internal fun convertToDateFormat_returnCorrectDate() {
        val year = 2020
        val month = 5
        val day = 30
        val dateStringFormat = Utility.convertToDateFormat(year, month, day)
        val expectedDateStringFormat = "30 May 2020"

        assertEquals(expectedDateStringFormat, dateStringFormat)
        println("Date is converted into correct string format.")
    }

    @Test
    internal fun convertToDateFormat_returnNull() {
        val year = 2020
        val month = 5
        val day = null
        val dateStringFormat = Utility.convertToDateFormat(year, month, day)
        val expectedDateStringFormat = null

        assertEquals(expectedDateStringFormat, dateStringFormat)
        println("Date is converted into null because day is null.")
    }

    @Test
    internal fun isBetweenTwoDates_returnTrue() {
        val dateFormat = SimpleDateFormat(Constant.ISO_DATE_TIME_FORMAT, Locale.US)
        val today = Calendar.getInstance().timeInMillis

        val tomorrow = dateFormat.format(today + 3600 * 24 * 1000)
        val yesterday = dateFormat.format(today - 3600 * 24 * 1000)

        assertEquals(true, Utility.isBetweenTwoDates(yesterday, tomorrow))
        println("$today is between $yesterday and $tomorrow.")
    }

    @Test
    internal fun isBetweenTwoDates_returnFalse() {
        val dateFormat = SimpleDateFormat(Constant.ISO_DATE_TIME_FORMAT, Locale.US)
        val today = Calendar.getInstance().timeInMillis

        val tomorrow = dateFormat.format(today + 3600 * 24 * 1000)
        val nextWeek = dateFormat.format(today + 3600 * 24 * 7 * 1000)

        assertEquals(false, Utility.isBetweenTwoDates(tomorrow, nextWeek))
        println("$today is not between $tomorrow and $nextWeek.")
    }

    @Test
    internal fun getCurrentSeason_returnTheCorrectSeason() {
        val currentSeason = Utility.getCurrentSeason()
        val expectedSeason = MediaSeason.safeValueOf(CURRENT_SEASON)

        assertEquals(expectedSeason, currentSeason)
        println("Current season is $CURRENT_SEASON")
    }
}