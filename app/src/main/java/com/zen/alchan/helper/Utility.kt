package com.zen.alchan.helper

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppColorTheme

object Utility {

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    fun timeDiffMoreThanOneDay(timeInMillis: Long?): Boolean {
        return System.currentTimeMillis() - (timeInMillis ?: 0) > 24 * 60 * 60 * 1000
    }
}