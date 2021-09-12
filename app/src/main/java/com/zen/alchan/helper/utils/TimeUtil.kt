package com.zen.alchan.helper.utils

import com.zen.alchan.data.response.anilist.FuzzyDate
import java.util.*

object TimeUtil {

    fun getCurrentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun getMillisFromFuzzyDate(fuzzyDate: FuzzyDate?): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, fuzzyDate?.year ?: 1)
        calendar.set(Calendar.MONTH, if (fuzzyDate?.month != null) fuzzyDate.month - 1 else 1)
        calendar.set(Calendar.DAY_OF_MONTH, fuzzyDate?.day ?: 1)
        return calendar.timeInMillis
    }
}