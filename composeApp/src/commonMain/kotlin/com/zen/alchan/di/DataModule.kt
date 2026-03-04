package com.zen.alchan.di

import com.zen.alchan.data.network.KtorHttpClient
import com.zen.alchan.data.provider.AniListApiProvider
import com.zen.alchan.data.provider.ApiProvider
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.ConfigRepository
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.DefaultAuthRepository
import com.zen.alchan.data.repository.DefaultConfigRepository
import com.zen.alchan.data.repository.DefaultContentRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataModule = module {
    single<ApiProvider> { AniListApiProvider(KtorHttpClient(get())) }
    single<AuthRepository> { DefaultAuthRepository() }
    single<ConfigRepository> { DefaultConfigRepository() }
    single<ContentRepository> { DefaultContentRepository(get()) }
}