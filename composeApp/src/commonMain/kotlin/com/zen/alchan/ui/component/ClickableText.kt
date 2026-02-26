package com.zen.alchan.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.ui.common.PreviewScreen

@Composable
fun ClickableText(
    text: String,
    textStyle: TextStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text,
        modifier = modifier.clickable(true, onClick = onClick),
        style = textStyle.copy(color = MaterialTheme.colorScheme.primary)
    )
}

@Composable
@Preview
fun PreviewScreen_ClickableText() {
    PreviewScreen {
        ClickableText(
            text = "Hello, World!",
            textStyle = MaterialTheme.typography.bodyLarge,
            onClick = { },
        )
    }
}