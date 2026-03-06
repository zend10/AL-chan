package com.zen.alchan.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.zen.alchan.data.provider.DataStoreLocalStorageProvider
import com.zen.alchan.data.provider.LocalStorageProvider
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun getLocalStorageModule(): Module

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

val previewLocalStorageModule = module {
    single<LocalStorageProvider> {
        val dataStore = createDataStore { "" }
        DataStoreLocalStorageProvider(dataStore)
    }
}