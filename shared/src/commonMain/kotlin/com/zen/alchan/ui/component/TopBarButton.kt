package com.zen.alchan.ui.component

import al_chan.shared.generated.resources.Res
import al_chan.shared.generated.resources.back
import al_chan.shared.generated.resources.ic_back
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.DefaultTheme
import com.zen.alchan.ui.common.PreviewScreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopBarButton(
    drawableResource: DrawableResource,
    contentDescription: StringResource,
    onClick: () -> Unit
) {
    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceDim),
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        DefaultImage(
            drawableResource = drawableResource,
            contentDescription = stringResource(contentDescription),
            modifier = Modifier
                .padding(DefaultTheme.dimen.paddingVerySmall)
                .size(DefaultTheme.dimen.iconNormal),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
@Preview
fun PreviewScreen_TopBarButton() {
    PreviewScreen { TopBarButton(Res.drawable.ic_back, Res.string.back, {}) }
}