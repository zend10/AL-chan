package com.zen.alchan.helper.utils

import java.util.*

object TimeUtil {

    fun getCurrentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }
}