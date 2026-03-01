package com.zen.alchan.di

import com.zen.alchan.ui.animelist.AnimeListViewModel
import com.zen.alchan.ui.base.DefaultDispatcher
import com.zen.alchan.ui.base.Dispatcher
import com.zen.alchan.ui.home.HomeViewModel
import com.zen.alchan.ui.landing.LandingViewModel
import com.zen.alchan.ui.main.MainViewModel
import com.zen.alchan.ui.mangalist.MangaListViewModel
import com.zen.alchan.ui.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureModule = module {
    factory<Dispatcher> { DefaultDispatcher() }
    viewModel { SplashViewModel(get(), get(), get()) }
    viewModel { LandingViewModel(get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { AnimeListViewModel(get()) }
    viewModel { MangaListViewModel(get()) }
}