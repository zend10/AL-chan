package com.zen.alchan.ui.landing

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.landing_footer
import al_chan.composeapp.generated.resources.landing_wallpaper
import al_chan.composeapp.generated.resources.press_to_start
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zen.alchan.DefaultTheme
import com.zen.alchan.helper.applyGradientOverlay
import com.zen.alchan.ui.common.PreviewScreen
import com.zen.alchan.ui.component.AppLogo
import com.zen.alchan.ui.component.ClickableText
import com.zen.alchan.ui.component.DefaultImage
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
    navigate(Landing) {
        popUpTo(graph.id)
    }
}

@Composable
fun LandingScreen(
    onNavigateToMain: () -> Unit,
) {
    val viewModel = koinViewModel<LandingViewModel>()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { newEffect ->
            when (newEffect) {
                LandingUiEffect.NavigateToMain -> onNavigateToMain()
            }
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { viewModel.onStartPressed() }
            )
    ) {
        DefaultImage(
            drawableResource = Res.drawable.landing_wallpaper,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().applyGradientOverlay()
        )
        AppLogo(Modifier.align(Alignment.Center))
        Column {
            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier.weight(1f)
                    .fillMaxWidth()
                    .padding(top = DefaultTheme.dimen.paddingBig),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(1f))
                PressToStatText()
                Spacer(Modifier.weight(1f))
                DisplayText(
                    text = stringResource(Res.string.landing_footer),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .safeDrawingPadding()
                        .padding(horizontal = DefaultTheme.dimen.paddingBig)
                        .padding(bottom = DefaultTheme.dimen.paddingBig)
                )
            }
        }
    }
}

@Composable
private fun PressToStatText() {
    val infiniteTransition = rememberInfiniteTransition(label = "startTextInfiniteTransition")
    val startTextAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse,
        )
    )
    ClickableText(
        text = if (startTextAnimation.value > 0.5)
            stringResource(Res.string.press_to_start)
        else
            "",
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            textMotion = TextMotion.Animated
        ),
        onClick = { }
    )
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