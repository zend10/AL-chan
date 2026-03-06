package com.zen.alchan.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.zen.alchan.data.provider.DataStoreLocalStorageProvider
import com.zen.alchan.data.provider.LocalStorageProvider
import com.zen.alchan.helper.LocalStorageConstant
import org.koin.core.module.Module
import org.koin.dsl.module

lateinit var androidContext: Context

actual fun getLocalStorageModule(): Module {
    return module {
        single<LocalStorageProvider> {
            val dataStore = createDataStore(androidContext)
            DataStoreLocalStorageProvider(dataStore)
        }
    }
}

fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(LocalStorageConstant.DATASTORE_FILE_NAME).absolutePath }
)