package com.zen.alchan.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.ui.common.PreviewScreen

@Composable
fun DisplayText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Text(
        text,
        modifier = modifier,
        style = textStyle.copy(color = MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
@Preview
fun PreviewScreen_DisplayText() {
    PreviewScreen {
        DisplayText(
            text = "Hello, World!",
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}