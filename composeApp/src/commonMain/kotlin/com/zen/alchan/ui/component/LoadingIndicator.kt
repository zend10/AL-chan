package com.zen.alchan.ui.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.ui.common.PreviewScreen

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
@Preview
fun PreviewScreen_LoadingIndicator() {
    PreviewScreen {
        LoadingIndicator()
    }
}

