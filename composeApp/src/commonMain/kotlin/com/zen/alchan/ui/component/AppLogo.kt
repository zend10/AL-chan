package com.zen.alchan.ui.component

import al_chan.composeapp.generated.resources.Res
import al_chan.composeapp.generated.resources.app_name
import al_chan.composeapp.generated.resources.app_name_subtitle
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.ui.common.PreviewScreen
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            stringResource(Res.string.app_name),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary
            ),
        )
        Text(
            stringResource(Res.string.app_name_subtitle),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
        )
    }
}

@Composable
@Preview
fun PreviewScreen_AppLogo() {
    PreviewScreen {
        AppLogo()
    }
}