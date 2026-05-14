package com.zen.alchan.di

import com.zen.alchan.data.provider.DataStoreLocalStorageProvider
import com.zen.alchan.data.provider.LocalStorageProvider
import org.koin.core.module.Module
import org.koin.dsl.module

expect val localStorageModule: Module

val previewLocalStorageModule = module {
    single<LocalStorageProvider> { DataStoreLocalStorageProvider(null) }
}