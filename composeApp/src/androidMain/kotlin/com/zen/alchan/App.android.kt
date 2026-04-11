package com.zen.alchan

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

private lateinit var applicationContext: Context

fun initApplicationContext(context: Context) {
    applicationContext = context
}

fun getApplicationContext(): Context {
    return applicationContext
}

actual fun navigateToWeb(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    getApplicationContext().startActivity(intent)
}