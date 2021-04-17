package com.zen.alchan

import android.app.Application
import com.google.gson.GsonBuilder
import com.zen.alchan.data.datasource.ContentDataSource
import com.zen.alchan.data.datasource.ContentDataSourceImpl
import com.zen.alchan.data.localstorage.SharedPreferencesManager
import com.zen.alchan.data.localstorage.SharedPreferencesManagerImpl
import com.zen.alchan.data.manager.UserManager
import com.zen.alchan.data.manager.UserManagerImpl
import com.zen.alchan.data.network.apollo.AniListApolloHandler
import com.zen.alchan.data.network.apollo.ApolloHandler
import com.zen.alchan.data.network.header.AniListHeaderInterceptorImpl
import com.zen.alchan.data.network.header.HeaderInterceptor
import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.ContentRepositoryImpl
import com.zen.alchan.helper.Constant
import com.zen.alchan.ui.home.HomeViewModel
import com.zen.alchan.ui.main.MainViewModel
import com.zen.alchan.ui.medialist.MediaListViewModel
import com.zen.alchan.ui.profile.ProfileViewModel

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ALchanApplication : Application() {

    private val appModules = module {
        val gson = GsonBuilder().serializeSpecialFloatingPointValues().create()

        // local storage
        single<SharedPreferencesManager> { SharedPreferencesManagerImpl(this@ALchanApplication.applicationContext, Constant.SHARED_PREFERENCES_NAME, gson) }

        // local storage manager
        single<UserManager> { UserManagerImpl(get()) }

        // network
        single<HeaderInterceptor> { AniListHeaderInterceptorImpl(get()) }
        single<ApolloHandler> { AniListApolloHandler(get()) }

        // data source
        single<ContentDataSource> { ContentDataSourceImpl(get()) }

        // repository
        single<ContentRepository> { ContentRepositoryImpl(get()) }

        // view model
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