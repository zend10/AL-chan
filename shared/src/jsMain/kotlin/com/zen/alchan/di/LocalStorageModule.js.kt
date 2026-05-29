package com.zen.alchan.di

import com.zen.alchan.data.provider.DefaultLocalStorageProvider
import com.zen.alchan.data.provider.LocalStorageProvider
import org.koin.dsl.module

actual val localStorageModule = module {
    single<LocalStorageProvider> { DefaultLocalStorageProvider() }
}