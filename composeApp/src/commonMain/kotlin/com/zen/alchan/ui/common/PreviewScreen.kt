package com.zen.alchan.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.zen.alchan.ALChanTheme
import com.zen.alchan.di.dataModule
import com.zen.alchan.di.featureModule
import org.koin.compose.KoinApplicationPreview

@Composable
fun PreviewScreen(screen: @Composable () -> Unit) {
    KoinApplicationPreview(application = {
        modules(dataModule, featureModule)
    }) {
        ALChanTheme {
            screen()
        }
    }
}