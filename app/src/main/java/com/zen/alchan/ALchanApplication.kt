package com.zen.alchan

import android.app.Application
import com.google.gson.GsonBuilder
import com.zen.alchan.data.constant.AniListConstant
import com.zen.alchan.data.constant.Constant
import com.zen.alchan.data.datasource.AuthenticationDataSource
import com.zen.alchan.data.datasource.ContentDataSource
import com.zen.alchan.data.datasource.DefaultAuthenticationDataSource
import com.zen.alchan.data.datasource.DefaultContentDataSource
import com.zen.alchan.data.localstorage.DefaultSharedPreferencesHandler
import com.zen.alchan.data.localstorage.SharedPreferencesHandler
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.manager.DefaultUserManager
import com.zen.alchan.data.network.apollo.AniListApolloHandler
import com.zen.alchan.data.network.apollo.ApolloHandler
import com.zen.alchan.data.network.header.AniListHeaderInterceptorImpl
import com.zen.alchan.data.network.header.HeaderInterceptor
import com.zen.alchan.data.repository.AuthenticationRepository
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.DefaultAuthenticationRepository
import com.zen.alchan.data.repository.DefaultContentRepository
import com.zen.alchan.ui.base.BaseActivityViewModel
import com.zen.alchan.ui.home.HomeViewModel
import com.zen.alchan.ui.main.MainViewModel
import com.zen.alchan.ui.main.SharedMainViewModel
import com.zen.alchan.ui.medialist.MediaListViewModel
import com.zen.alchan.ui.profile.ProfileViewModel

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class ALchanApplication : Application() {

    private val appModules = module {
        val gson = GsonBuilder().serializeSpecialFloatingPointValues().create()

        // constant
        single<Constant>(named("aniListConstant")) { AniListConstant() }

        // local storage
        single<SharedPreferencesHandler> {
            DefaultSharedPreferencesHandler(
                this@ALchanApplication.applicationContext,
                BuildConfig.APPLICATION_ID + ".LocalStorage",
                gson
            )
        }

        // local storage manager
        single<UserManager> { DefaultUserManager(get()) }

        // network
        single<HeaderInterceptor> { AniListHeaderInterceptorImpl(get()) }
        single<ApolloHandler> {
            val baseUrl = get<Constant>(named("aniListConstant")).baseUrl
            AniListApolloHandler(get(), baseUrl)
        }

        // data source
        single<AuthenticationDataSource> { DefaultAuthenticationDataSource(get()) }
        single<ContentDataSource> { DefaultContentDataSource(get()) }

        // repository
        single<AuthenticationRepository> { DefaultAuthenticationRepository(get(), get()) }
        single<ContentRepository> { DefaultContentRepository(get()) }

        // view model
        viewModel { BaseActivityViewModel() }

        viewModel { SharedMainViewModel() }
        viewModel { MainViewModel(get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { MediaListViewModel() }
        viewModel { ProfileViewModel() }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ALchanApplication)
            modules(appModules)
        }
    }
}