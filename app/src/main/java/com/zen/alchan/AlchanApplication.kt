package com.zen.alchan

import android.app.Application
import com.zen.alchan.data.datasource.AuthDataSource
import com.zen.alchan.data.datasource.AuthDataSourceImpl
import com.zen.alchan.data.localstorage.LocalStorage
import com.zen.alchan.data.localstorage.LocalStorageImpl
import com.zen.alchan.data.network.ApolloHandler
import com.zen.alchan.data.network.HeaderInterceptor
import com.zen.alchan.data.network.HeaderInterceptorImpl
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.AuthRepositoryImpl
import com.zen.alchan.helper.Constant
import com.zen.alchan.ui.auth.LoginViewModel
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.auth.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class AlchanApplication : Application() {

    private val appModules = module {
        single<LocalStorage> { LocalStorageImpl(this@AlchanApplication.applicationContext, Constant.SHARED_PREFERENCES_NAME) }
        single<HeaderInterceptor> { HeaderInterceptorImpl(get()) }
        single { ApolloHandler(get()) }

        single<AuthDataSource> { AuthDataSourceImpl(get()) }
        single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
        viewModel { BaseViewModel(get()) }
        viewModel { SplashViewModel(get()) }
        viewModel { LoginViewModel(get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AlchanApplication)
            modules(appModules)
        }
    }
}