package com.zen.alchan.helper

import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun Modifier.applyGradientOverlay(heightRatio: Float = 0.3f): Modifier {
    val backgroundColor = MaterialTheme.colorScheme.background
    return drawWithCache {
        val gradient = Brush.verticalGradient(
            colors = listOf(Color.Transparent, backgroundColor),
            startY = size.height * heightRatio,
            endY = size.height,
        )
        onDrawWithContent {
            drawContent()
            drawRect(gradient, blendMode = BlendMode.Multiply)
        }
    }
}

@Composable
fun Modifier.applyWidthFromScreenWidth(ratio: Float): Modifier {
    return width(getScreenSize() * ratio)
}

@Composable
expect fun getScreenSize(): Dp