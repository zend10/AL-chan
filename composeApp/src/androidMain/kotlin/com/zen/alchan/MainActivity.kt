package com.zen.alchan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        DeeplinkHandler.handleDeeplink(intent.data.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(
                koinAppDeclaration = {
                    androidContext(this@MainActivity.applicationContext)
                },
                onNavigateToWeb = { navigateToWeb(it) }
            )
        }
    }

    private fun navigateToWeb(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}