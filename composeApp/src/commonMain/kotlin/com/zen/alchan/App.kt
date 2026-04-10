package com.zen.alchan

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.zen.alchan.di.dataModule
import com.zen.alchan.di.featureModule
import com.zen.alchan.di.localStorageModule
import com.zen.alchan.di.networkModule
import com.zen.alchan.ui.landing.landingDestination
import com.zen.alchan.ui.landing.navigateToLanding
import com.zen.alchan.ui.main.mainDestination
import com.zen.alchan.ui.main.navigateToMain
import com.zen.alchan.ui.splash.Splash
import com.zen.alchan.ui.splash.splashDestination
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(localStorageModule, networkModule, dataModule, featureModule)
    }) {
        val navController = rememberNavController()
        ALChanTheme {
            NavHost(navController = navController, startDestination = Splash) {
                splashDestination(
                    onNavigateToLanding = { navController.navigateToLanding() },
                    onNavigateToMain = { navController.navigateToMain() }
                )
                landingDestination(
                    onNavigateToMain = { navController.navigateToMain() }
                )
                mainDestination()
            }
        }
    }
}