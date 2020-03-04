package com.zen.alchan.helper

import android.app.Activity
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


fun Activity.changeStatusBarColor(color: Int) {
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color
}

fun Double.removeTrailingZero(): String {
    val format = DecimalFormat("#.#")
    return format.format(this)
}

fun Int.secondsToDateTime(): String {
    val dateFormat = SimpleDateFormat(Constant.DATE_TIME_FORMAT, Locale.US)
    val date = Date(this * 1000L)
    return dateFormat.format(date)
}
