package com.zen.alchan.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.DefaultTheme
import com.zen.alchan.ui.common.PreviewScreen

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier, text: String = "") {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
        DisplayText(
            text,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = DefaultTheme.dimen.paddingVerySmall)
        )
    }

}

@Composable
@Preview
fun PreviewScreen_LoadingIndicator() {
    PreviewScreen {
        LoadingIndicator(text = "Loading")
    }
}

