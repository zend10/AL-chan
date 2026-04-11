package com.zen.alchan.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.zen.alchan.helper.LocalStorageConstant

private lateinit var applicationContext: Context

fun initPreferencesDataStore(appContext: Context) {
    applicationContext = appContext
}

fun getPreferencesDataStorePath(appContext: Context): String =
    appContext.filesDir.resolve(LocalStorageConstant.DATASTORE_FILE_NAME).absolutePath

actual fun createPreferencesDataStore(dataStoreFileName: String): DataStore<Preferences> {
    val path = getPreferencesDataStorePath(applicationContext)
    return getPreferencesDataStore(path)
}