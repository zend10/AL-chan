package com.zen.alchan

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.zen.alchan.di.dataModule
import com.zen.alchan.di.featureModule
import com.zen.alchan.di.localStorageModule
import com.zen.alchan.di.networkModule
import com.zen.alchan.ui.calendar.calendarDestination
import com.zen.alchan.ui.calendar.navigateToCalendar
import com.zen.alchan.ui.explore.exploreDestination
import com.zen.alchan.ui.explore.navigateToExplore
import com.zen.alchan.ui.landing.landingDestination
import com.zen.alchan.ui.landing.navigateToLanding
import com.zen.alchan.ui.main.mainDestination
import com.zen.alchan.ui.main.navigateToMain
import com.zen.alchan.ui.maindetail.mainDetailDestination
import com.zen.alchan.ui.seasonal.navigateToSeasonal
import com.zen.alchan.ui.seasonal.seasonalDestination
import com.zen.alchan.ui.social.navigateToSocial
import com.zen.alchan.ui.social.socialDestination
import com.zen.alchan.ui.splash.Splash
import com.zen.alchan.ui.splash.splashDestination
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(localStorageModule, networkModule, dataModule, featureModule)
    }) {
        val viewModel = koinViewModel<AppViewModel>()
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { newEffect ->
                when (newEffect) {
                    AppUiEffect.NavigateToSeasonal -> navController.navigateToSeasonal()
                }
            }
        }

        LaunchedEffect(Unit) {
            DeeplinkHandler.deeplinkListener = object : DeeplinkListener {
                override fun onDeeplinkReceived(deeplink: String) {
                    viewModel.onDeeplinkReceived(deeplink)
                }
            }
        }

        ALChanTheme {
            NavHost(
                navController = navController,
                startDestination = Splash,
                enterTransition = {
                    slideInHorizontally()
                },
                exitTransition = {
                    slideOutHorizontally()
                },
                popEnterTransition = {
                    slideInHorizontally()
                },
                popExitTransition = {
                    slideOutHorizontally()
                }
            ) {
                splashDestination(
                    onNavigateToLanding = { navController.navigateToLanding() },
                    onNavigateToMain = { navController.navigateToMain() }
                )
                landingDestination(
                    onNavigateToMain = { navController.navigateToMain() }
                )
                mainDestination(
                    onNavigateToSeasonal = { navController.navigateToSeasonal() },
                    onNavigateToExplore = { navController.navigateToExplore() },
                    onNavigateToCalendar = { navController.navigateToCalendar() },
                    onNavigateToSocial = { navController.navigateToSocial() },
                    onNavigateToWeb = { navigateToWeb(it) }
                )
                seasonalDestination()
                exploreDestination()
                calendarDestination()
                socialDestination()
                mainDetailDestination()
            }
        }
    }
}

expect fun navigateToWeb(url: String)
