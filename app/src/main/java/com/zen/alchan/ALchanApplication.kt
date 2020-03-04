package com.zen.alchan

import android.app.Application
import com.zen.alchan.data.datasource.MediaListDataSource
import com.zen.alchan.data.datasource.MediaListDataSourceImpl
import com.zen.alchan.data.datasource.UserDataSource
import com.zen.alchan.data.datasource.UserDataSourceImpl
import com.zen.alchan.data.localstorage.*
import com.zen.alchan.data.network.ApolloHandler
import com.zen.alchan.data.network.HeaderInterceptor
import com.zen.alchan.data.network.HeaderInterceptorImpl
import com.zen.alchan.data.repository.*
import com.zen.alchan.helper.Constant
import com.zen.alchan.ui.MainViewModel
import com.zen.alchan.ui.animelist.AnimeListItemViewModel
import com.zen.alchan.ui.animelist.AnimeListViewModel
import com.zen.alchan.ui.auth.LoginViewModel
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.auth.SplashViewModel
import com.zen.alchan.ui.home.HomeViewModel
import com.zen.alchan.ui.profile.ProfileViewModel
import com.zen.alchan.ui.profile.bio.BioViewModel
import com.zen.alchan.ui.settings.anilist.AniListSettingsViewModel
import com.zen.alchan.ui.settings.app.AppSettingsViewModel
import com.zen.alchan.ui.settings.list.ListSettingsViewModel
import com.zen.alchan.ui.settings.notifications.NotificationsSettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ALchanApplication : Application() {

    private val appModules = module {
        single<LocalStorage> { LocalStorageImpl(this@ALchanApplication.applicationContext, Constant.SHARED_PREFERENCES_NAME) }
        single<AppSettingsManager> { AppSettingsManagerImpl(get()) }
        single<UserManager> { UserManagerImpl(get()) }

        single<HeaderInterceptor> { HeaderInterceptorImpl(get()) }
        single { ApolloHandler(get()) }

        single<UserDataSource> { UserDataSourceImpl(get()) }
        single<MediaListDataSource> { MediaListDataSourceImpl(get()) }

        single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
        single<UserRepository> { UserRepositoryImpl(get(), get()) }
        single<AppSettingsRepository> { AppSettingsRepositoryImpl(get())}
        single<MediaListRepository> { MediaListRepositoryImpl(get(), get()) }

        viewModel { BaseViewModel(get()) }

        viewModel { SplashViewModel(get()) }
        viewModel { LoginViewModel(get()) }

        viewModel { MainViewModel(get(), get()) }

        viewModel { HomeViewModel(get(), get()) }

        viewModel { AnimeListViewModel(get()) }
        viewModel { AnimeListItemViewModel(get(), get()) }

        viewModel { ProfileViewModel(get()) }
        viewModel { BioViewModel(get()) }
        viewModel { AppSettingsViewModel(get()) }
        viewModel { AniListSettingsViewModel(get()) }
        viewModel { ListSettingsViewModel(get()) }
        viewModel { NotificationsSettingsViewModel(get()) }
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