package com.zen.alchan.ui.common

import androidx.compose.runtime.Composable
import com.zen.alchan.ALChanTheme
import com.zen.alchan.di.dataModule
import com.zen.alchan.di.featureModule
import com.zen.alchan.di.networkModule
import com.zen.alchan.di.previewLocalStorageModule
import org.koin.compose.KoinApplicationPreview

@Composable
fun PreviewScreen(screen: @Composable () -> Unit) {
    KoinApplicationPreview(application = {
        modules(previewLocalStorageModule, networkModule, dataModule, featureModule)
    }) {
        ALChanTheme {
            screen()
        }
    }
}