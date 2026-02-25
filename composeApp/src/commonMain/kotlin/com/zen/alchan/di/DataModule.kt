package com.zen.alchan.di

import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.ConfigRepository
import com.zen.alchan.data.repository.DefaultAuthRepository
import com.zen.alchan.data.repository.DefaultConfigRepository
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { DefaultAuthRepository() }
    single<ConfigRepository> { DefaultConfigRepository() }
}