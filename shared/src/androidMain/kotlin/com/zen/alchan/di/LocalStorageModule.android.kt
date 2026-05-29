package com.zen.alchan.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.zen.alchan.data.provider.DataStoreLocalStorageProvider
import com.zen.alchan.data.provider.LocalStorageProvider
import com.zen.alchan.helper.LocalStorageConstant
import okio.Path.Companion.toPath
import org.koin.dsl.module

private lateinit var dataStore: DataStore<Preferences>

private fun getDataStore(context: Context): DataStore<Preferences> {
    if (!::dataStore.isInitialized) {
        dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { getPreferencesDataStorePath(context).toPath() }
        )
    }
    return dataStore
}

fun getPreferencesDataStorePath(appContext: Context): String =
    appContext.filesDir.resolve(LocalStorageConstant.DATASTORE_FILE_NAME).absolutePath

actual val localStorageModule = module {
    single<LocalStorageProvider> { DataStoreLocalStorageProvider(getDataStore(get())) }
}