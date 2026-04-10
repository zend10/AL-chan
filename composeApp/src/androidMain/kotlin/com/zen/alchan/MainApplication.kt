package com.zen.alchan

import android.app.Application
import com.zen.alchan.di.initPreferencesDataStore

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initPreferencesDataStore(applicationContext)
    }
}