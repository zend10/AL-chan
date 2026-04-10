package com.zen.alchan.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.zen.alchan.data.provider.DataStoreLocalStorageProvider
import com.zen.alchan.data.provider.LocalStorageProvider
import com.zen.alchan.data.provider.PreviewLocalStorageProvider
import okio.Path.Companion.toPath
import org.koin.dsl.module

fun getPreferencesDataStore(path: String) = PreferenceDataStoreFactory.createWithPath {
    path.toPath()
}

expect fun createPreferencesDataStore(): DataStore<Preferences>

val localStorageModule = module {
    single<DataStore<Preferences>> { createPreferencesDataStore() }
    single<LocalStorageProvider> { DataStoreLocalStorageProvider(get()) }
}

val previewLocalStorageModule = module {
    single<LocalStorageProvider> { PreviewLocalStorageProvider() }
}