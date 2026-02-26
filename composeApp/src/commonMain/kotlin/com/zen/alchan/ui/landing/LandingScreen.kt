package com.zen.alchan.ui.landing

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.landing_footer
import al_chan.composeapp.generated.resources.press_to_start
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zen.alchan.DefaultTheme
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.AppLogo
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.DisplayText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Landing

fun NavGraphBuilder.landingDestination(
    onNavigateToMain: () -> Unit,
) {
    composable<Landing> { LandingScreen(onNavigateToMain) }
}

fun NavController.navigateToLanding() {
    navigate(Landing)
}

@Composable
fun LandingScreen(
    onNavigateToMain: () -> Unit,
) {
    val viewModel = koinViewModel<LandingViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { newEffect ->
            when (newEffect) {
                LandingUiEffect.NavigateToMain -> onNavigateToMain()
                is LandingUiEffect.OpenWebView -> TODO()
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(DefaultTheme.dimen.paddingBig),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        AppLogo()
        Spacer(Modifier.weight(1f))
        ClickableText(
            text = stringResource(Res.string.press_to_start),
            textStyle = MaterialTheme.typography.bodyLarge,
            onClick = onNavigateToMain,
        )
        Spacer(Modifier.weight(1f))
        DisplayText(
            text = stringResource(Res.string.landing_footer),
            textStyle = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Center
            ),
        )
    }
}

@Composable
@Preview(
    device = Devices.PHONE,
    showSystemUi = true,
)
fun PreviewPhone_LandingScreen() {
    PreviewScreen {
        LandingScreen(
            onNavigateToMain = { }
        )
    }
}

@Composable
@Preview(
    device = Devices.TABLET,
    showSystemUi = true,
)
fun PreviewTablet_LandingScreen() {
    PreviewScreen {
        LandingScreen(
            onNavigateToMain = { }
        )
    }
}