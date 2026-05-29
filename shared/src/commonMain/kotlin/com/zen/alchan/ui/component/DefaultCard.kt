package com.zen.alchan.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.ui.common.PreviewScreen

@Composable
fun DefaultCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.elevatedShape,
    content: @Composable (ColumnScope.() -> Unit)
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = shape,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
@Preview
fun PreviewScreen_DefaultCard() {
    PreviewScreen {
        DefaultCard {
            Text("Hello, World!")
        }
    }
}