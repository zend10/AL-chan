package com.zen.alchan.ui.landing

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zen.alchan.helper.PreviewConfig
import com.zen.alchan.theme.AppColor
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.Logo
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

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Logo()
    }
}

@Composable
@Preview(
    widthDp = PreviewConfig.Phone.WIDTH,
    heightDp = PreviewConfig.Phone.HEIGHT,
    showBackground = true
)
fun PreviewPhone_LandingScreen() {
    PreviewScreen {
        LandingScreen(
            onNavigateToLogin = { },
            onNavigateToMain = { },
        )
    }
}

@Composable
@Preview(
    widthDp = PreviewConfig.Tablet.WIDTH,
    heightDp = PreviewConfig.Tablet.HEIGHT,
    showBackground = true
)
fun PreviewTablet_LandingScreen() {
    PreviewScreen {
        LandingScreen(
            onNavigateToLogin = { },
            onNavigateToMain = { },
        )
    }
}