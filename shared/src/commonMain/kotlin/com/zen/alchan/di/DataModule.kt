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
import com.zen.alchan.helper.AniListConstant
import org.koin.dsl.module

val dataModule = module {
    single<ApiProvider> {
        AniListApiProvider(
            KtorHttpClient(get(), AniListConstant.ANILIST_GRAPHQL_BASE_URL),
            AniListConstant
        )
    }
    single<AuthRepository> { DefaultAuthRepository(get(), get()) }
    single<ConfigRepository> { DefaultConfigRepository(get()) }
    single<ContentRepository> { DefaultContentRepository(get()) }
}