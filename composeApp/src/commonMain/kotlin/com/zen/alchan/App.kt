package com.zen.alchan

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.zen.alchan.di.dataModule
import com.zen.alchan.di.featureModule
import com.zen.alchan.ui.landing.Landing
import com.zen.alchan.ui.landing.landingDestination
import com.zen.alchan.ui.login.loginDestination
import com.zen.alchan.ui.login.navigateToLogin
import com.zen.alchan.ui.main.mainDestination
import com.zen.alchan.ui.main.navigateToMain
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(dataModule, featureModule)
    }) {
        val navController = rememberNavController()
        ALChanTheme {
            NavHost(navController = navController, startDestination = Landing) {
                landingDestination(
                    onNavigateToLogin = { navController.navigateToLogin() },
                    onNavigateToMain = { navController.navigateToMain() }
                )
                loginDestination()
                mainDestination()
            }
        }
    }
}