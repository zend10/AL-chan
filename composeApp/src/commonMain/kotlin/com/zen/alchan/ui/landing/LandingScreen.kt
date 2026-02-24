package com.zen.alchan.ui.landing

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Landing

fun NavGraphBuilder.landingDestination(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
) {
    composable<Landing> { LandingScreen(onNavigateToLogin, onNavigateToMain) }
}

fun NavController.navigateToLanding() {
    navigate(Landing)
}

@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
) {
    val viewModel = koinViewModel<LandingViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { newEffect ->
            when (newEffect) {
                LandingUiEffect.NavigateToLogin -> onNavigateToLogin()
                LandingUiEffect.NavigateToMain -> onNavigateToMain()
            }
        }
    }

    Text("Landing")
}
