package com.zen.alchan.ui.component

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.landing_wallpaper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.ui.common.PreviewScreen
import org.jetbrains.compose.resources.painterResource

@Composable
fun GradientOverlayBox(
    modifier: Modifier = Modifier,
    content: @Composable (BoxScope.() -> Unit)
) {
    val transparentOverlay = MaterialTheme.colorScheme.surfaceDim
    Box(
        modifier = modifier
            .drawWithCache {
                val gradient = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, transparentOverlay),
                    startY = size.height / 2,
                    endY = size.height,
                )
                onDrawWithContent {
                    drawRect(gradient, blendMode = BlendMode.Multiply)
                    drawContent()
                }
            },
        content = content
    )
}

@Composable
@Preview
fun PreviewScreen_GradientOverlayBox() {
    PreviewScreen {
        GradientOverlayBox {

        }
    }
}