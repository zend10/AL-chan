package com.zen.alchan.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.AppLogo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Splash

fun NavGraphBuilder.splashDestination(
    onNavigateToLanding: () -> Unit,
    onNavigateToMain: () -> Unit,
) {
    composable<Splash> { SplashScreen(onNavigateToLanding, onNavigateToMain) }
}

fun NavController.navigateToSplash() {
    navigate(Splash)
}

@Composable
fun SplashScreen(
    onNavigateToLanding: () -> Unit,
    onNavigateToMain: () -> Unit,
) {
    val viewModel = koinViewModel<SplashViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { newEffect ->
            when (newEffect) {
                SplashUiEffect.NavigateToLanding -> onNavigateToLanding()
                SplashUiEffect.NavigateToMain -> onNavigateToMain()
            }
        }
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AppLogo()
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true,
)
fun PreviewPhone_SplashScreen() {
    PreviewScreen {
        SplashScreen(
            onNavigateToLanding = { },
            onNavigateToMain = { },
        )
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true,
)
fun PreviewTablet_SplashScreen() {
    PreviewScreen {
        SplashScreen(
            onNavigateToLanding = { },
            onNavigateToMain = { },
        )
    }
}