package com.zen.alchan

import android.app.Application

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initApplicationContext(applicationContext)
    }
}