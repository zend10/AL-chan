package com.zen.alchan

import android.app.Application
import android.content.Context
import com.zen.alchan.di.androidContext

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        androidContext = applicationContext
    }
}