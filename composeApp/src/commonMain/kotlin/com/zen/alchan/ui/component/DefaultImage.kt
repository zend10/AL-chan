package com.zen.alchan.ui.component

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.ic_anime
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.zen.alchan.ui.common.PreviewScreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun DefaultImage(
    imageUrl: String? = null,
    drawableResource: DrawableResource? = null,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            imageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            colorFilter = colorFilter,
            contentScale = contentScale
        )
    } else if (drawableResource != null) {
        Image(
            painterResource(drawableResource),
            contentDescription = contentDescription,
            modifier = modifier,
            colorFilter = colorFilter,
            contentScale = contentScale
        )
    } else {
        Box(modifier)
    }
}

@Composable
@Preview
fun PreviewScreen_DefaultImage() {
    PreviewScreen {
        DefaultImage(
            drawableResource = Res.drawable.ic_anime,
            contentDescription = ""
        )
    }
}