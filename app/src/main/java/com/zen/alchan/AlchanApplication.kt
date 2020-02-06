package com.zen.alchan

import android.app.Application
import com.zen.alchan.data.localstorage.LocalStorage
import com.zen.alchan.data.localstorage.LocalStorageImpl
import com.zen.alchan.data.repository.AuthRepository
import com.zen.alchan.data.repository.AuthRepositoryImpl
import com.zen.alchan.helper.Constant
import com.zen.alchan.ui.auth.LoginViewModel
import com.zen.alchan.ui.base.BaseViewModel
import com.zen.alchan.ui.auth.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class AlchanApplication : Application() {

    private val appModules = module {
        single<LocalStorage> { LocalStorageImpl(this@AlchanApplication.applicationContext, Constant.SHARED_PREFERENCES_NAME) }

        single<AuthRepository> { AuthRepositoryImpl(get()) }
        viewModel { BaseViewModel(get()) }
        viewModel { SplashViewModel(get()) }
        viewModel { LoginViewModel(get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AlchanApplication)
            modules(appModules)
        }
    }
}