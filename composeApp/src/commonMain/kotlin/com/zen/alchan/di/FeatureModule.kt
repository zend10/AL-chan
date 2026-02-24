package com.zen.alchan.di

import com.zen.alchan.ui.base.DefaultDispatcher
import com.zen.alchan.ui.base.Dispatcher
import com.zen.alchan.ui.landing.LandingViewModel
import com.zen.alchan.ui.login.LoginViewModel
import com.zen.alchan.ui.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureModule = module {
    factory<Dispatcher> { DefaultDispatcher() }
    viewModel { LandingViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { MainViewModel(get()) }
}